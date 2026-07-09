package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.IsbnBookDetail;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.IsbnLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/lookup")
@RequiredArgsConstructor
public class IsbnLookupController {

  private final IsbnLookupService isbnLookupService;

  @GetMapping("/{isbn}")
  public ResponseEntity<?> lookup(@PathVariable String isbn) {
    try {
      IsbnBookDetail result = isbnLookupService.lookup(isbn);
      return ResponseEntity.ok(result);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
