package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.Exception.StockAlreadyExists;
import com.infirmary.backend.configuration.Exception.StockNotFoundException;
import com.infirmary.backend.configuration.dto.StockDTO;
import com.infirmary.backend.configuration.model.Location;
import com.infirmary.backend.configuration.model.Stock;
import com.infirmary.backend.configuration.repository.LocationRepository;
import com.infirmary.backend.configuration.repository.PrescriptionMedsRepository;
import com.infirmary.backend.configuration.repository.StockRepository;
import com.infirmary.backend.configuration.service.StockService;
import com.infirmary.backend.shared.utility.FunctionUtil;
import com.infirmary.backend.shared.utility.MessageConfigUtil;
import com.infirmary.backend.shared.utility.StockThreshold;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final MessageConfigUtil messageConfigUtil;
    private final LocationRepository locationRepository;
    private final PrescriptionMedsRepository prescriptionMedsRepository;

    public StockServiceImpl(StockRepository stockRepository, MessageConfigUtil messageConfigUtil,
            LocationRepository locationRepository, PrescriptionMedsRepository prescriptionMedsRepository) {
        this.stockRepository = stockRepository;
        this.messageConfigUtil = messageConfigUtil;
        this.locationRepository = locationRepository;
        this.prescriptionMedsRepository = prescriptionMedsRepository;
    }

    @Override
    public List<StockDTO> getStockByTypeIn(List<String> type) throws StockNotFoundException {
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Type not found");
        }
        List<Stock> byMedicineTypeIn = stockRepository.findByMedicineTypeIn(type);
        List<StockDTO> list = byMedicineTypeIn.stream().map(StockDTO::new).toList();
        if (list.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return list;
    }

    @Override
    public List<StockDTO> getStockBeforeExpirationDate(LocalDate expirationDate) throws StockNotFoundException {
        List<Stock> byExpirationDate = stockRepository.findByExpirationDateBefore(expirationDate);
        List<StockDTO> list = byExpirationDate.stream().map(StockDTO::new).toList();
        if (list.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return list;
    }

    @Override
    public List<StockDTO> getStockAfterExpirationDate(LocalDate expirationDate) throws StockNotFoundException {
        List<Stock> byExpirationDate = stockRepository.findByExpirationDateAfter(expirationDate);
        List<StockDTO> list = byExpirationDate.stream().map(StockDTO::new).toList();
        if (list.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return list;
    }

    @Override
    public List<StockDTO> getStockByQuantityGreaterEqualThan() throws StockNotFoundException {
        Long quantity = StockThreshold.DEFAULT_THRESHOLD.getL();
        List<Stock> list = stockRepository.findByQuantityGreaterThanEqualOrderByQuantityDesc(quantity);
        List<StockDTO> listDTO = list.stream().map(StockDTO::new).toList();
        if (listDTO.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return listDTO;
    }

    @Override
    public List<StockDTO> getStocksByCompanyNameIn(List<String> companyNames) throws StockNotFoundException {
        if (companyNames.isEmpty()) {
            throw new IllegalArgumentException("Company names not found");
        }
        List<Stock> byCompany = stockRepository.findByCompanyIn(companyNames);
        List<StockDTO> list = byCompany.stream().map(StockDTO::new).toList();
        if (list.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return list;
    }

    @Override
    public StockDTO getStockByBatchNumber(Long batchNumber) throws StockNotFoundException {
        if (Objects.isNull(batchNumber)) {
            throw new IllegalArgumentException("Batch Number not found");
        }
        Optional<Stock> byBatchNumber = stockRepository.findByBatchNumber(batchNumber);
        if (byBatchNumber.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return new StockDTO(byBatchNumber.get());
    }

    @Override
    public List<StockDTO> getNullStock() throws StockNotFoundException {
        List<Stock> list = stockRepository.findByQuantityNull();
        List<StockDTO> dtoList = list.stream().map(StockDTO::new).toList();
        if (dtoList.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        return dtoList;
    }

    @Override
    public StockDTO addStock(StockDTO stockDTO, Long locId) throws StockAlreadyExists {
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new ResourceNotFoundException("No Location Found"));

        // Check for existing stock with all 4 criteria
        List<Stock> existingStocks = stockRepository.findByMedicineNameAndBatchNumberAndLocationAndExpirationDate(
                stockDTO.getMedicineName(),
                stockDTO.getBatchNumber(),
                location,
                stockDTO.getExpirationDate());

        if (!existingStocks.isEmpty()) {
            // Merge quantities of all matching stocks
            Stock firstStock = existingStocks.get(0);
            long totalQuantity = existingStocks.stream()
                    .mapToLong(Stock::getQuantity)
                    .sum();

            firstStock.setQuantity(totalQuantity + stockDTO.getQuantity());
            Stock updatedStock = stockRepository.save(firstStock);

            // Handle duplicates (soft delete or reassign prescriptions)
            for (int i = 1; i < existingStocks.size(); i++) {
                Stock duplicateStock = existingStocks.get(i);

                // Check if the duplicate stock is referenced in prescription_medicine
                if (prescriptionMedsRepository.existsByMedicine(duplicateStock)) {
                    // Reassign prescriptions to the first stock
                    prescriptionMedsRepository.reassignPrescriptions(duplicateStock, firstStock);
                }

                // Soft delete the duplicate stock (set quantity to 0)
                duplicateStock.setQuantity(0L);
                stockRepository.save(duplicateStock);
            }

            return new StockDTO(updatedStock);
        } else {
            // Create new stock with all fields
            Stock stock = new Stock(stockDTO);
            stock.setLocation(location);
            Stock savedStock = stockRepository.save(stock);
            return new StockDTO(savedStock);
        }
    }

    @Override
    public String editStock(StockDTO stockDTO, Long locId) {
        Stock originalStock = stockRepository.findById(stockDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock Not Found"));

        Location newLocation = locationRepository.findById(locId)
                .orElseThrow(() -> new ResourceNotFoundException("No location found"));

        // Check for existing stock with new values
        List<Stock> existingStocks = stockRepository.findByMedicineNameAndBatchNumberAndLocationAndExpirationDate(
                stockDTO.getMedicineName(),
                stockDTO.getBatchNumber(),
                newLocation,
                stockDTO.getExpirationDate());

        if (!existingStocks.isEmpty() && !existingStocks.get(0).getId().equals(originalStock.getId())) {
            // Merge quantities
            Stock targetStock = existingStocks.get(0);
            targetStock.setQuantity(targetStock.getQuantity() + originalStock.getQuantity());
            stockRepository.save(targetStock);

            // Delete original stock
            stockRepository.delete(originalStock);
        } else {
            // Handle prescription constraints
            if (prescriptionMedsRepository.existsByMedicine(originalStock)) {
                if (!stockDTO.getCompany().equals(originalStock.getCompany()) ||
                        !stockDTO.getComposition().equals(originalStock.getComposition()) ||
                        !stockDTO.getMedicineName().equals(originalStock.getMedicineName()) ||
                        !stockDTO.getMedicineType().equals(originalStock.getMedicineType())) {
                    throw new IllegalArgumentException("The Stock has been prescribed");
                }
            }

            // Update all fields including location and expiration date
            originalStock.setBatchNumber(stockDTO.getBatchNumber());
            originalStock.setCompany(stockDTO.getCompany());
            originalStock.setComposition(stockDTO.getComposition());
            originalStock.setExpirationDate(stockDTO.getExpirationDate());
            originalStock.setLocation(newLocation);
            originalStock.setMedicineName(stockDTO.getMedicineName());
            originalStock.setMedicineType(stockDTO.getMedicineType());
            originalStock.setQuantity(stockDTO.getQuantity());

            stockRepository.save(originalStock);
        }

        return "Stock updated successfully";
    }

    public byte[] exportStocksToExcel() throws IOException {
        List<Stock> stocks = stockRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Medicine Stocks");

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Batch Number", "Medicine Name", "Composition", "Quantity", "Medicine Type",
                    "Expiration Date", "Company", "Location" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Stock stock : stocks) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stock.getBatchNumber());
                row.createCell(1).setCellValue(stock.getMedicineName());
                row.createCell(2).setCellValue(stock.getComposition());
                row.createCell(3).setCellValue(stock.getQuantity());
                row.createCell(4).setCellValue(stock.getMedicineType());
                row.createCell(5).setCellValue(stock.getExpirationDate().toString());
                row.createCell(6).setCellValue(stock.getCompany());
                row.createCell(7).setCellValue(stock.getLocation().getLocationName());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findByQuantityGreaterThan(0L);
    }

    @Override
    public void deleteStock(UUID stockUuid) throws StockNotFoundException {
        Optional<Stock> batch = stockRepository.findById(stockUuid);
        if (batch.isEmpty()) {
            throw new StockNotFoundException(messageConfigUtil.getStockNotFound());
        }
        Stock newStock = batch.get();
        newStock.setQuantity(0L);
        stockRepository.save(newStock);
    }

    @Override
    public List<Stock> getAvailableStock(Double longitude, Double latitude) {
        if (longitude == null || latitude == null)
            throw new IllegalArgumentException("No Location Mentioned");

        List<Location> locations = locationRepository.findAll();
        Location presentLocation = null;

        for (Location location : locations) {
            if (FunctionUtil.IsWithinRadius(location.getLatitude(), location.getLongitude(), latitude, longitude)) {
                presentLocation = location;
                break;
            }
        }

        if (presentLocation == null)
            throw new IllegalArgumentException("Must be present on location");

        return stockRepository.findByQuantityGreaterThanAndLocationAndExpirationDateAfter(
                0L,
                presentLocation,
                Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate());
    }
}