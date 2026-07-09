package com.example.demo.repository;

import com.example.demo.entity.SaleItem;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleItemRepository extends JpaRepository<SaleItem, java.util.UUID> {

  @Query(
      """
      SELECT si FROM SaleItem si
      JOIN FETCH si.bookCopy bc
      JOIN FETCH bc.book
      WHERE si.sale.saleDate >= :start AND si.sale.saleDate < :end
      ORDER BY si.sale.saleDate
      """)
  List<SaleItem> findBySaleDateBetween(@Param("start") Instant start, @Param("end") Instant end);
}
