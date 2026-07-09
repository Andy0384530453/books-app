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
public class BookCopy {

  @Id private UUID idBookCopy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_book", nullable = false)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FormatType format;

  @Column(nullable = false)
  private Integer stock;

  @Column private Float sellingPrice;

  public Float effectiveSellingPrice() {
    return sellingPrice != null ? sellingPrice : book.getSellingPrice();
  }

  public enum FormatType {
    POCHE,
    BROCHE,
    RELIE,
    CARTONNE
  }
}
