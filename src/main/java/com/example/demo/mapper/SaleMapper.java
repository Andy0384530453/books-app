package com.example.demo.mapper;

import com.example.demo.dto.CustomerDetail;
import com.example.demo.dto.SaleDetail;
import com.example.demo.dto.SaleItemDetail;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Sale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public SaleDetail toDetail(Sale sale) {
    if (sale == null) return null;

    Customer c = sale.getCustomer();
    CustomerDetail custDetail = null;
    if (c != null) {
      custDetail =
          CustomerDetail.builder()
              .idCustomer(c.getIdCustomer())
              .lastName(c.getLastName())
              .firstName(c.getFirstName())
              .phoneNumber(c.getPhoneNumber())
              .email(c.getEmail())
              .build();
    }

    var itemDetails =
        sale.getItems().stream()
            .map(
                si -> {
                  var copy = si.getBookCopy();
                  String bookTitle = copy.getBook() != null ? copy.getBook().getTitle() : "Unknown";
                  return SaleItemDetail.builder()
                      .idCopy(copy.getIdBookCopy())
                      .bookTitle(bookTitle)
                      .format(copy.getFormat().name())
                      .quantity(si.getQuantity())
                      .unitPrice(si.getUnitPrice())
                      .build();
                })
            .collect(Collectors.toSet());

    float total =
        itemDetails.stream()
            .map(sid -> sid.getUnitPrice() * sid.getQuantity())
            .reduce(0f, Float::sum);

    return SaleDetail.builder()
        .idSale(sale.getIdSale())
        .saleDate(sale.getSaleDate())
        .paymentStatus(sale.getPaymentStatus().name())
        .total(total)
        .customer(custDetail)
        .items(itemDetails)
        .build();
  }
}
