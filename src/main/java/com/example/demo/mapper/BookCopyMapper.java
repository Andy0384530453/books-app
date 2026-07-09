package com.example.demo.mapper;

import com.example.demo.dto.BookCopyDetail;
import com.example.demo.entity.BookCopy;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {

  public BookCopyDetail toDetail(BookCopy copy) {
    if (copy == null) return null;

    return BookCopyDetail.builder()
        .idCopy(copy.getIdBookCopy())
        .bookId(copy.getBook().getIdBook())
        .bookTitle(copy.getBook().getTitle())
        .format(copy.getFormat().name())
        .sellingPrice(copy.effectiveSellingPrice())
        .stock(copy.getStock())
        .build();
  }
}
