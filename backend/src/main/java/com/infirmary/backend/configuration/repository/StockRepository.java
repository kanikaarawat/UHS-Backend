package com.infirmary.backend.configuration.repository;

import com.infirmary.backend.configuration.model.Location;
import com.infirmary.backend.configuration.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

        // Existing methods
        List<Stock> findByMedicineTypeIn(List<String> type);

        List<Stock> findByExpirationDateBefore(LocalDate expirationDate);

        List<Stock> findByExpirationDateAfter(LocalDate expirationDate);

        List<Stock> findByQuantityGreaterThanEqualOrderByQuantityDesc(Long quantity);

        List<Stock> findByCompanyIn(List<String> companyNames);

        Optional<Stock> findByBatchNumber(Long batchNumber);

        List<Stock> findByQuantityNull();

        List<Stock> findByQuantityGreaterThan(Long quantity);

        List<Stock> findByQuantityGreaterThanAndLocationAndExpirationDateAfter(
                        Long quantity,
                        Location location,
                        LocalDate expirationDate);

        List<Stock> findByMedicineNameAndBatchNumberAndLocationAndExpirationDate(
                        String medicineName,
                        Long batchNumber,
                        Location location,
                        LocalDate expirationDate);
}