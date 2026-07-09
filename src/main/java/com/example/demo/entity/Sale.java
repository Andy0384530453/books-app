package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sale")
public class Sale {

  @Id private UUID idSale;

  @Column(nullable = false)
  private Instant saleDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus paymentStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_customer")
  private Customer customer;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<SaleItem> items = new HashSet<>();

  public enum PaymentStatus {
    PENDING,
    PAID,
    CANCELLED
  }
}
