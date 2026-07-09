package com.example.demo.mapper;

import com.example.demo.dto.AuthorDetail;
import com.example.demo.dto.BookDetail;
import com.example.demo.dto.CategoryDetail;
import com.example.demo.entity.Book;
import com.example.demo.entity.Category;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public BookDetail toDetail(Book book) {
    if (book == null) return null;

    Category cat = book.getCategory();
    CategoryDetail catDetail = null;
    if (cat != null) {
      catDetail =
          CategoryDetail.builder()
              .idCategory(cat.getIdCategory())
              .categoryName(cat.getCategoryName().name())
              .build();
    }

    var authDetails =
        book.getAuthors().stream()
            .map(
                a ->
                    AuthorDetail.builder()
                        .idAuthor(a.getIdAuthor())
                        .lastName(a.getLastName())
                        .firstName(a.getFirstName())
                        .build())
            .collect(Collectors.toSet());

    return BookDetail.builder()
        .idBook(book.getIdBook())
        .title(book.getTitle())
        .genre(book.getGenre())
        .publicationDate(book.getPublicationDate())
        .purchasePrice(book.getPurchasePrice())
        .sellingPrice(book.getSellingPrice())
        .currentStock(0)
        .category(catDetail)
        .authors(authDetails)
        .build();
  }
}
