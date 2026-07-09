package com.example.demo.service;

import com.example.demo.dto.BookCopyRevenue;
import com.example.demo.dto.BookCopyRevenue.PeriodType;
import com.example.demo.dto.FormatRevenue;
import com.example.demo.entity.SaleItem;
import com.example.demo.repository.SaleItemRepository;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevenueService {

  private static final ZoneId ZONE = ZoneId.of("Europe/Paris");
  private final SaleItemRepository saleItemRepository;

  public BookCopyRevenue getMonthlyRevenue(int year, int month) {
    YearMonth ym = YearMonth.of(year, month);
    Instant start = ym.atDay(1).atStartOfDay(ZONE).toInstant();
    Instant end = ym.plusMonths(1).atDay(1).atStartOfDay(ZONE).toInstant();

    List<SaleItem> items = saleItemRepository.findBySaleDateBetween(start, end);
    return buildRevenue(items, ym.toString(), PeriodType.MONTHLY);
  }

  public BookCopyRevenue getYearlyRevenue(int year) {
    Instant start = YearMonth.of(year, 1).atDay(1).atStartOfDay(ZONE).toInstant();
    Instant end = YearMonth.of(year + 1, 1).atDay(1).atStartOfDay(ZONE).toInstant();

    List<SaleItem> items = saleItemRepository.findBySaleDateBetween(start, end);
    return buildRevenue(items, String.valueOf(year), PeriodType.YEARLY);
  }

  private BookCopyRevenue buildRevenue(List<SaleItem> items, String period, PeriodType type) {
    Map<String, List<SaleItem>> byFormat =
        items.stream().collect(Collectors.groupingBy(si -> si.getBookCopy().getFormat().name()));

    List<FormatRevenue> details =
        byFormat.entrySet().stream()
            .map(
                entry -> {
                  String format = entry.getKey();
                  List<SaleItem> formatItems = entry.getValue();
                  int copiesSold = formatItems.stream().mapToInt(SaleItem::getQuantity).sum();
                  float revenue =
                      formatItems.stream()
                          .map(si -> si.getUnitPrice() * si.getQuantity())
                          .reduce(0f, Float::sum);
                  float avgUnitPrice = copiesSold > 0 ? revenue / copiesSold : 0f;
                  return FormatRevenue.builder()
                      .format(format)
                      .copiesSold(copiesSold)
                      .revenue(revenue)
                      .avgUnitPrice(avgUnitPrice)
                      .build();
                })
            .collect(Collectors.toList());

    float total = details.stream().map(FormatRevenue::getRevenue).reduce(0f, Float::sum);

    return BookCopyRevenue.builder()
        .period(period)
        .periodType(type)
        .details(details)
        .total(total)
        .build();
  }
}
