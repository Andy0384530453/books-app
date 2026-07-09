package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.SaleDetail;
import com.example.demo.dto.SaleInput;
import com.example.demo.exception.BadRequestException;
import com.example.demo.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

  private final SaleService saleService;

  @PostMapping
  public ResponseEntity<SaleDetail> createSale(@RequestBody SaleInput input) {
    try {
      SaleDetail sale = saleService.createSale(input);
      return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
