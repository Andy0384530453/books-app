package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.StockMovementDetail;
import com.example.demo.dto.StockRequest;
import com.example.demo.dto.StockUpdateResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.StockMovementService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books/{bookId}/copies/{copyId}/stock")
@RequiredArgsConstructor
public class StockMovementController {

  private final StockMovementService stockMovementService;

  @PostMapping("/increment")
  public ResponseEntity<StockUpdateResponse> increment(
      @PathVariable UUID bookId, @PathVariable UUID copyId, @RequestBody StockRequest request) {
    try {
      StockUpdateResponse result = stockMovementService.increment(bookId, copyId, request);
      return ResponseEntity.ok(result);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/decrement")
  public ResponseEntity<?> decrement(
      @PathVariable UUID bookId, @PathVariable UUID copyId, @RequestBody StockRequest request) {
    try {
      StockUpdateResponse result = stockMovementService.decrement(bookId, copyId, request);
      return ResponseEntity.ok(result);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/history")
  public ResponseEntity<List<StockMovementDetail>> history(
      @PathVariable UUID bookId, @PathVariable UUID copyId) {
    try {
      List<StockMovementDetail> movements = stockMovementService.history(bookId, copyId);
      return ResponseEntity.ok(movements);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
