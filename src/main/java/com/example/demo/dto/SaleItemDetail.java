package com.example.demo.dto;

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
public class SaleItemDetail {
  private UUID idCopy;
  private String bookTitle;
  private String format;
  private Integer quantity;
  private Float unitPrice;
}
