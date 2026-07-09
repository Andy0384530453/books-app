package com.example.demo.service;

import com.example.demo.dto.BookCopyDetail;
import com.example.demo.entity.BookCopy;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BookCopyMapper;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCopyService {

  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final BookCopyMapper mapper;

  public List<BookCopyDetail> findAllByBookId(UUID bookId) {
    bookRepository
        .findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
    return bookCopyRepository.findByBookIdBook(bookId).stream()
        .map(mapper::toDetail)
        .collect(Collectors.toList());
  }

  public BookCopyDetail findById(UUID bookId, UUID copyId) {
    bookRepository
        .findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
    BookCopy copy =
        bookCopyRepository
            .findById(copyId)
            .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + copyId));
    return mapper.toDetail(copy);
  }
}
