package com.example.demo.mapper;

import com.example.demo.dto.StockMovementDetail;
import com.example.demo.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

  public StockMovementDetail toDetail(StockMovement m) {
    if (m == null) return null;

    return StockMovementDetail.builder()
        .idMovement(m.getIdMovement())
        .movementType(m.getMovementType().name())
        .movementDate(m.getMovementDate())
        .quantity(m.getQuantity())
        .saleId(m.getSale() != null ? m.getSale().getIdSale() : null)
        .reason(m.getReason())
        .build();
  }
}
