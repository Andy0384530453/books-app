package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.BookCopyDetail;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BookCopyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/copies")
@RequiredArgsConstructor
public class BookCopyController {

  private final BookCopyService bookCopyService;

  @GetMapping
  public ResponseEntity<List<BookCopyDetail>> getCopiesByBook(@PathVariable UUID bookId) {
    try {
      List<BookCopyDetail> copies = bookCopyService.findAllByBookId(bookId);
      return ResponseEntity.ok(copies);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{copyId}")
  public ResponseEntity<BookCopyDetail> getCopyById(
      @PathVariable UUID bookId, @PathVariable UUID copyId) {
    try {
      BookCopyDetail copy = bookCopyService.findById(bookId, copyId);
      return ResponseEntity.ok(copy);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
