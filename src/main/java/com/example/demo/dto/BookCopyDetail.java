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
public class BookCopyDetail {
  private UUID idCopy;
  private UUID bookId;
  private String bookTitle;
  private String format;
  private Float sellingPrice;
  private Integer stock;
}
