package com.example.demo.dto;

import java.time.Instant;
import java.util.Set;
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
public class SaleDetail {
  private UUID idSale;
  private Instant saleDate;
  private String paymentStatus;
  private Float total;
  private CustomerDetail customer;
  private Set<SaleItemDetail> items;
}
