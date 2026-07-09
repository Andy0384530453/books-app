package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stock_movement")
public class StockMovement {

  @Id private UUID idMovement;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_copy", nullable = false)
  private BookCopy bookCopy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MovementType movementType;

  @Column(nullable = false)
  private Instant movementDate;

  @Column(nullable = false)
  private Integer quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sale_id")
  private Sale sale;

  private String reason;

  public enum MovementType {
    IN,
    OUT
  }
}
