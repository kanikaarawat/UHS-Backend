package com.infirmary.backend.configuration.repository;

import com.infirmary.backend.configuration.model.Location;
import com.infirmary.backend.configuration.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
        LocalDate expirationDate
    );
    
    List<Stock> findByMedicineNameAndBatchNumberAndLocationAndExpirationDate(
        String medicineName,
        Long batchNumber,
        Location location,
        LocalDate expirationDate
    );

    // New queries for export functionality
    @Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN ?1 AND ?2 AND s.quantity > ?3")
    List<Stock> findByExpirationDateBetweenAndQuantityGreaterThan(
        LocalDate startDate, 
        LocalDate endDate,
        Long quantity
    );
    
    @Query("SELECT s FROM Stock s WHERE s.quantity BETWEEN ?1 AND ?2")
    List<Stock> findByQuantityBetween(Long minQuantity, Long maxQuantity);
    
    @Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN ?1 AND ?2 AND s.quantity BETWEEN ?3 AND ?4")
    List<Stock> findByExpirationDateBetweenAndQuantityBetween(
        LocalDate startDate, 
        LocalDate endDate, 
        Long minQuantity,
        Long maxQuantity
    );
    
    // Existing query methods
    @Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN ?1 AND ?2")
    List<Stock> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Stock s WHERE s.expirationDate BETWEEN ?1 AND ?2 AND s.quantity < ?3")
    List<Stock> findByExpirationDateBetweenAndQuantityLessThan(
        LocalDate startDate, 
        LocalDate endDate, 
        Long quantity
    );
    
    List<Stock> findByQuantityLessThan(Long quantity);
}