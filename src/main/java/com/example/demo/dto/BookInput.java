package com.example.demo.dto;

import java.util.Set;
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
public class BookInput {
  private String title;
  private Float purchasePrice;
  private Float sellingPrice;
  private String categoryId;
  private Set<String> authorIds;
  private String genre;
}
