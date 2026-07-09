package com.example.demo.service;

import com.example.demo.dto.SaleDetail;
import com.example.demo.dto.SaleInput;
import com.example.demo.dto.SaleItemInput;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Sale;
import com.example.demo.entity.Sale.PaymentStatus;
import com.example.demo.entity.SaleItem;
import com.example.demo.entity.StockMovement;
import com.example.demo.entity.StockMovement.MovementType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.mapper.SaleMapper;
import com.example.demo.repository.BookCopyRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.SaleRepository;
import com.example.demo.repository.StockMovementRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final CustomerRepository customerRepository;
  private final BookCopyRepository bookCopyRepository;
  private final StockMovementRepository stockMovementRepository;
  private final SaleMapper saleMapper;

  @Transactional
  public SaleDetail createSale(SaleInput input) {
    if (input.getItems() == null || input.getItems().isEmpty()) {
      throw new BadRequestException("Sale must have at least one item");
    }

    Customer customer =
        customerRepository
            .findById(UUID.fromString(input.getCustomerId()))
            .orElseThrow(
                () -> new BadRequestException("Customer not found: " + input.getCustomerId()));

    // Load all BookCopies and check stock BEFORE creating the sale
    Map<UUID, BookCopy> copies = new HashMap<>();
    Map<UUID, Integer> requested = new HashMap<>();
    for (SaleItemInput item : input.getItems()) {
      UUID copyId = UUID.fromString(item.getIdCopy());
      int qty = item.getQuantity();
      if (qty <= 0) {
        throw new BadRequestException("Quantity must be positive for copy: " + copyId);
      }
      BookCopy copy =
          bookCopyRepository
              .findById(copyId)
              .orElseThrow(() -> new BadRequestException("Copy not found: " + copyId));
      if (copy.getStock() < qty) {
        throw new BadRequestException(
            "Insufficient stock for copy "
                + copyId
                + " ("
                + copy.getBook().getTitle()
                + " — "
                + copy.getFormat().name()
                + "): have "
                + copy.getStock()
                + ", requested "
                + qty);
      }
      copies.put(copyId, copy);
      requested.put(copyId, qty);
    }

    // All stock checks passed — create and persist the sale first
    UUID saleId = UUID.randomUUID();
    Sale sale =
        Sale.builder()
            .idSale(saleId)
            .saleDate(Instant.now())
            .paymentStatus(PaymentStatus.PAID)
            .customer(customer)
            .items(new HashSet<>())
            .build();

    // Build sale items and deduct stock
    Set<SaleItem> saleItems = new HashSet<>();
    for (SaleItemInput item : input.getItems()) {
      UUID copyId = UUID.fromString(item.getIdCopy());
      BookCopy copy = copies.get(copyId);
      int qty = requested.get(copyId);

      copy.setStock(copy.getStock() - qty);
      bookCopyRepository.save(copy);

      SaleItem saleItem =
          SaleItem.builder()
              .idSaleItem(UUID.randomUUID())
              .sale(sale)
              .bookCopy(copy)
              .quantity(qty)
              .unitPrice(copy.effectiveSellingPrice())
              .build();
      saleItems.add(saleItem);
    }

    sale.setItems(saleItems);
    Sale saved = saleRepository.save(sale);

    // Record stock movements after sale is persisted
    for (SaleItemInput item : input.getItems()) {
      UUID copyId = UUID.fromString(item.getIdCopy());
      BookCopy copy = copies.get(copyId);
      int qty = requested.get(copyId);

      stockMovementRepository.save(
          StockMovement.builder()
              .idMovement(UUID.randomUUID())
              .bookCopy(copy)
              .movementType(MovementType.OUT)
              .movementDate(Instant.now())
              .quantity(qty)
              .sale(saved)
              .reason("Sale")
              .build());
    }

    return saleMapper.toDetail(saved);
  }
}
