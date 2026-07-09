package com.example.demo.service;

import com.example.demo.dto.StockMovementDetail;
import com.example.demo.dto.StockRequest;
import com.example.demo.dto.StockUpdateResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.StockMovement;
import com.example.demo.entity.StockMovement.MovementType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.StockMovementMapper;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.StockMovementRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockMovementService {

  private final StockMovementRepository stockMovementRepository;
  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final StockMovementMapper mapper;

  @Transactional
  public StockUpdateResponse increment(UUID bookId, UUID copyId, StockRequest request) {
    if (request.getQuantity() == null || request.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be positive");
    }
    bookRepository
        .findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
    BookCopy copy =
        bookCopyRepository
            .findById(copyId)
            .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + copyId));

    copy.setStock(copy.getStock() + request.getQuantity());
    bookCopyRepository.save(copy);

    StockMovement movement =
        StockMovement.builder()
            .idMovement(UUID.randomUUID())
            .bookCopy(copy)
            .movementType(MovementType.IN)
            .movementDate(Instant.now())
            .quantity(request.getQuantity())
            .reason("Restock")
            .build();
    stockMovementRepository.save(movement);

    return StockUpdateResponse.builder().copyId(copyId).newStock(copy.getStock()).build();
  }

  @Transactional
  public StockUpdateResponse decrement(UUID bookId, UUID copyId, StockRequest request) {
    if (request.getQuantity() == null || request.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be positive");
    }

    bookRepository
        .findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
    BookCopy copy =
        bookCopyRepository
            .findById(copyId)
            .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + copyId));

    int remaining = copy.getStock() - request.getQuantity();
    if (remaining < 0) {
      throw new BadRequestException("Insufficient stock");
    }

    copy.setStock(remaining);
    bookCopyRepository.save(copy);

    StockMovement movement =
        StockMovement.builder()
            .idMovement(UUID.randomUUID())
            .bookCopy(copy)
            .movementType(MovementType.OUT)
            .movementDate(Instant.now())
            .quantity(request.getQuantity())
            .reason("Sale")
            .build();
    stockMovementRepository.save(movement);

    return StockUpdateResponse.builder().copyId(copyId).newStock(copy.getStock()).build();
  }

  @Transactional(readOnly = true)
  public List<StockMovementDetail> history(UUID bookId, UUID copyId) {
    bookRepository
        .findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
    bookCopyRepository
        .findById(copyId)
        .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + copyId));

    return stockMovementRepository.findByBookCopyIdBookCopyOrderByMovementDateDesc(copyId).stream()
        .map(mapper::toDetail)
        .collect(Collectors.toList());
  }
}
