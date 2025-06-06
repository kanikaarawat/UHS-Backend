package com.infirmary.backend.configuration.impl;
import com.infirmary.backend.configuration.repository.CurrentAppointmentRepository;
import com.infirmary.backend.configuration.Exception.StockAlreadyExists;
import com.infirmary.backend.configuration.Exception.StockNotFoundException;
import com.infirmary.backend.configuration.dto.StockDTO;
import com.infirmary.backend.configuration.model.CurrentAppointment;
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
    private final CurrentAppointmentRepository currentAppointmentRepository;


    public StockServiceImpl(StockRepository stockRepository,
                        MessageConfigUtil messageConfigUtil,
                        LocationRepository locationRepository,
                        PrescriptionMedsRepository prescriptionMedsRepository,
                        CurrentAppointmentRepository currentAppointmentRepository) {
    this.stockRepository = stockRepository;
    this.messageConfigUtil = messageConfigUtil;
    this.locationRepository = locationRepository;
    this.prescriptionMedsRepository = prescriptionMedsRepository;
    this.currentAppointmentRepository = currentAppointmentRepository;
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
    
        // Fetch existing stock excluding the current one
        List<Stock> existingStocks = stockRepository.findByMedicineNameAndBatchNumberAndLocationAndExpirationDate(
                stockDTO.getMedicineName(),
                stockDTO.getBatchNumber(),
                newLocation,
                stockDTO.getExpirationDate());
    
        // Filter out the stock being edited
        existingStocks = existingStocks.stream()
                .filter(stock -> !stock.getId().equals(originalStock.getId()))
                .toList();
    
        if (!existingStocks.isEmpty()) {
            // Merge quantities with the first matching stock
            Stock targetStock = existingStocks.get(0);
    
            // Add quantities
            targetStock.setQuantity(targetStock.getQuantity() + originalStock.getQuantity());
    
            // Save the merged stock
            stockRepository.save(targetStock);
    
            // Reassign prescriptions if the original stock is referenced
            if (prescriptionMedsRepository.existsByMedicine(originalStock)) {
                prescriptionMedsRepository.reassignPrescriptions(originalStock, targetStock);
            }
    
            // Delete the original stock
            stockRepository.delete(originalStock);
    
            return "Stocks merged successfully";
        } else {
            // Handle prescription constraints on changes
            if (prescriptionMedsRepository.existsByMedicine(originalStock)) {
                boolean criticalChange = 
                    !stockDTO.getCompany().equals(originalStock.getCompany()) ||
                    !stockDTO.getComposition().equals(originalStock.getComposition()) ||
                    !stockDTO.getMedicineName().equals(originalStock.getMedicineName()) ||
                    !stockDTO.getMedicineType().equals(originalStock.getMedicineType());
    
                if (criticalChange) {
                    throw new IllegalArgumentException("The Stock has been prescribed. Critical fields cannot be modified.");
                }
            }
    
            // Update the original stock fields
            originalStock.setBatchNumber(stockDTO.getBatchNumber());
            originalStock.setCompany(stockDTO.getCompany());
            originalStock.setComposition(stockDTO.getComposition());
            originalStock.setExpirationDate(stockDTO.getExpirationDate());
            originalStock.setLocation(newLocation);
            originalStock.setMedicineName(stockDTO.getMedicineName());
            originalStock.setMedicineType(stockDTO.getMedicineType());
            originalStock.setQuantity(stockDTO.getQuantity());
    
            stockRepository.save(originalStock);
    
            return "Stock updated successfully";
        }
    }    

    @Override
    public byte[] exportStocksToExcel(String filter) throws IOException {
        List<Stock> allStocks;
        LocalDate today = LocalDate.now();
        
        switch (filter.toLowerCase()) {
            case "all":
                allStocks = stockRepository.findAll();
                break;
            case "expiring":
                LocalDate nextMonth = today.plusMonths(1);
                allStocks = stockRepository.findByExpirationDateBetweenAndQuantityGreaterThan(
                    today, 
                    nextMonth, 
                    0L
                );
                break;
            case "low":
                allStocks = stockRepository.findByQuantityBetween(1L, 49L);
                break;
            case "expiring-low":
                LocalDate nextMonthCombined = today.plusMonths(1);
                allStocks = stockRepository.findByExpirationDateBetweenAndQuantityBetween(
                    today, 
                    nextMonthCombined, 
                    1L, 
                    49L
                );
                break;
            case "available":
                allStocks = stockRepository.findByQuantityGreaterThan(0L);
                break;
            default:
                throw new IllegalArgumentException("Invalid filter type: " + filter);
        }
    
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    
            Sheet sheet = workbook.createSheet("Medicine Stocks");
            
            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                "Batch Number", "Medicine Name", "Composition", "Quantity",
                "Medicine Type", "Expiration Date", "Company", "Location", "Status"
            };
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
    
            int rowNum = 1;
            for (Stock stock : allStocks) {
                Row row = sheet.createRow(rowNum++);
                String status = stock.getQuantity() > 0 ? "Active" : "Deleted";
                populateStockRow(row, stock, status);
            }
    
            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
    
            workbook.write(out);
            return out.toByteArray();
        }
    }
private void populateStockRow(Row row, Stock stock, String status) {
        row.createCell(0).setCellValue(stock.getBatchNumber());
        row.createCell(1).setCellValue(stock.getMedicineName());
        row.createCell(2).setCellValue(stock.getComposition());
        row.createCell(3).setCellValue(stock.getQuantity());
        row.createCell(4).setCellValue(stock.getMedicineType());
        row.createCell(5).setCellValue(stock.getExpirationDate().toString());
        row.createCell(6).setCellValue(stock.getCompany());
        row.createCell(7).setCellValue(stock.getLocation().getLocationName());
        row.createCell(8).setCellValue(status);
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
    @Override
    public List<Stock> getAvailableStockByDoctorLocation(String doctorEmail) {
        Optional<CurrentAppointment> optionalAppointment = currentAppointmentRepository.findByDoctor_DoctorEmail(doctorEmail);
    
        if (optionalAppointment.isEmpty() || optionalAppointment.get().getDoctor() == null ||
            optionalAppointment.get().getDoctor().getLocation() == null) {
            throw new IllegalArgumentException("Doctor is not checked in or no valid location assigned.");
        }
    
        Location location = optionalAppointment.get().getDoctor().getLocation();
    
        return stockRepository.findByQuantityGreaterThanAndLocationAndExpirationDateAfter(
                0L,
                location,
                Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate());
    }
    
}