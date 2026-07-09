package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sale_item")
public class SaleItem {

  @Id private UUID idSaleItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sale", nullable = false)
  private Sale sale;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_copy", nullable = false)
  private BookCopy bookCopy;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Float unitPrice;
}
