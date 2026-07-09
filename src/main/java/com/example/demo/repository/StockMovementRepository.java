package com.example.demo.repository;

import com.example.demo.entity.StockMovement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
  List<StockMovement> findByBookCopyIdBookCopyOrderByMovementDateDesc(UUID idCopy);
}
