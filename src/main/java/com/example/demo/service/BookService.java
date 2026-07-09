package com.example.demo.service;

import com.example.demo.dto.BookDetail;
import com.example.demo.dto.BookInput;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Category;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BookMapper;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookCopyRepository bookCopyRepository;
  private final CategoryRepository categoryRepository;
  private final AuthorRepository authorRepository;
  private final BookMapper mapper;

  public List<BookDetail> findAll(String genre) {
    List<Book> books;
    if (genre != null && !genre.isBlank()) {
      books = bookRepository.findByGenreIgnoreCase(genre);
    } else {
      books = bookRepository.findAll();
    }
    return books.stream().map(this::toDetailWithStock).collect(Collectors.toList());
  }

  public BookDetail findById(UUID id) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    return toDetailWithStock(book);
  }

  public BookDetail create(BookInput input) {
    if (input.getTitle() == null || input.getTitle().isBlank()) {
      throw new BadRequestException("Title is required");
    }
    if (input.getPurchasePrice() == null || input.getSellingPrice() == null) {
      throw new BadRequestException("Purchase and selling prices are required");
    }

    Category category = null;
    if (input.getCategoryId() != null && !input.getCategoryId().isBlank()) {
      category =
          categoryRepository
              .findById(UUID.fromString(input.getCategoryId()))
              .orElseThrow(
                  () -> new BadRequestException("Category not found: " + input.getCategoryId()));
    }

    Set<Author> authors = Set.of();
    if (input.getAuthorIds() != null && !input.getAuthorIds().isEmpty()) {
      authors =
          input.getAuthorIds().stream()
              .map(
                  id ->
                      authorRepository
                          .findById(UUID.fromString(id))
                          .orElseThrow(() -> new BadRequestException("Author not found: " + id)))
              .collect(Collectors.toSet());
    }

    Book book =
        Book.builder()
            .idBook(UUID.randomUUID())
            .title(input.getTitle())
            .genre(input.getGenre())
            .purchasePrice(input.getPurchasePrice())
            .sellingPrice(input.getSellingPrice())
            .category(category)
            .authors(authors)
            .build();

    Book saved = bookRepository.save(book);
    return toDetailWithStock(saved);
  }

  private BookDetail toDetailWithStock(Book book) {
    BookDetail detail = mapper.toDetail(book);
    int totalStock =
        bookCopyRepository.findByBookIdBook(book.getIdBook()).stream()
            .mapToInt(BookCopy::getStock)
            .sum();
    detail.setCurrentStock(totalStock);
    return detail;
  }
}
