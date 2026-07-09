package com.example.demo.dto;

import java.util.List;
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
public class BookCopyRevenue {
  private String period;
  private PeriodType periodType;
  private List<FormatRevenue> details;
  private Float total;

  public enum PeriodType {
    MONTHLY,
    YEARLY
  }
}
