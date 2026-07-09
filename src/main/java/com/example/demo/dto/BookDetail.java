package com.example.demo.dto;

import java.time.LocalDate;
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
public class BookDetail {
  private UUID idBook;
  private String title;
  private String genre;
  private LocalDate publicationDate;
  private Float purchasePrice;
  private Float sellingPrice;
  private Integer currentStock;
  private CategoryDetail category;
  private Set<AuthorDetail> authors;
}
