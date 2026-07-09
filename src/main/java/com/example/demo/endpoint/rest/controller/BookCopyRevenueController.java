package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.BookCopyRevenue;
import com.example.demo.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book-copies/revenues")
@RequiredArgsConstructor
public class BookCopyRevenueController {

  private final RevenueService revenueService;

  @GetMapping
  public ResponseEntity<BookCopyRevenue> getRevenues(
      @RequestParam String period,
      @RequestParam int year,
      @RequestParam(required = false, defaultValue = "1") int month) {

    BookCopyRevenue result;
    switch (period.toLowerCase()) {
      case "monthly":
        result = revenueService.getMonthlyRevenue(year, month);
        break;
      case "yearly":
        result = revenueService.getYearlyRevenue(year);
        break;
      default:
        return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(result);
  }
}
