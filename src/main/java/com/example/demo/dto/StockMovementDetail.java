package com.example.demo.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDetail {
  private UUID idMovement;
  private String movementType;
  private Instant movementDate;
  private Integer quantity;
  private UUID saleId;
  private String reason;
}
