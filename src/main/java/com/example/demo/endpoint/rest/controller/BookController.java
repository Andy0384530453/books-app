package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.BookDetail;
import com.example.demo.dto.BookInput;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BookService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public ResponseEntity<List<BookDetail>> getAllBooks(
      @RequestParam(required = false) String genre) {
    try {
      List<BookDetail> books = bookService.findAll(genre);
      return ResponseEntity.ok(books);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookDetail> getBookById(@PathVariable UUID id) {
    try {
      BookDetail book = bookService.findById(id);
      return ResponseEntity.ok(book);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping
  public ResponseEntity<BookDetail> createBook(@RequestBody BookInput input) {
    try {
      BookDetail book = bookService.create(input);
      return ResponseEntity.status(HttpStatus.CREATED).body(book);
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
