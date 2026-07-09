package com.example.demo.dto;

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
public class FormatRevenue {
  private String format;
  private Integer copiesSold;
  private Float revenue;
  private Float avgUnitPrice;
}
