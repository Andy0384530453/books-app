package com.example.demo.conf;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

class BookStoreIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  static final String BASE = "/books";

  // ---------- Categories exist from seed ----------
  static final UUID CAT_MEDIUM = UUID.fromString("aa000000-0000-0000-0000-000000000002");
  static final UUID CAT_SMALL = UUID.fromString("aa000000-0000-0000-0000-000000000003");

  // ---------- Authors exist from seed ----------
  static final UUID AUTH_ROW = UUID.fromString("bb000000-0000-0000-0000-000000000001");
  static final UUID AUTH_TOLK = UUID.fromString("bb000000-0000-0000-0000-000000000002");

  // ---------- Books exist from seed ----------
  static final UUID BOOK_HP = UUID.fromString("cc000000-0000-0000-0000-000000000001");
  static final UUID BOOK_LOTR = UUID.fromString("cc000000-0000-0000-0000-000000000003");

  // ---------- BookCopies from seed ----------
  static final UUID COPY_HP_P = UUID.fromString("dd000000-0000-0000-0000-000000000001"); // POCHE
  static final UUID COPY_HP_B = UUID.fromString("dd000000-0000-0000-0000-000000000002"); // BROCHE
  static final UUID COPY_LOTR_B = UUID.fromString("dd000000-0000-0000-0000-000000000011"); // BROCHE

  // ---------- Customers from seed ----------
  static final UUID CUST_JEAN = UUID.fromString("ee000000-0000-0000-0000-000000000001");
  static final UUID CUST_MARIE = UUID.fromString("ee000000-0000-0000-0000-000000000002");

  @Test
  void getAllBooks() {
    var resp = rest.getForEntity(BASE, String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertNotNull(resp.getBody());
  }

  @Test
  void getBookById() {
    var resp = rest.getForEntity(BASE + "/" + BOOK_HP, String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("Harry Potter"));
  }

  @Test
  void getBookById_notFound() {
    var resp = rest.getForEntity(BASE + "/" + UUID.randomUUID(), String.class);
    assertEquals(404, resp.getStatusCode().value());
  }

  @Test
  void getBooksByGenre() {
    var resp = rest.getForEntity(BASE + "?genre=Fantasy", String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("Harry Potter"));
    assertTrue(resp.getBody().contains("Seigneur"));
  }

  @Test
  void getCopiesByBook() {
    var resp = rest.getForEntity(BASE + "/" + BOOK_HP + "/copies", String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("POCHE"));
    assertTrue(resp.getBody().contains("BROCHE"));
  }

  @Test
  void getCopyById() {
    var resp = rest.getForEntity(BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P, String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("POCHE"));
  }

  @Test
  void getCopyById_notFound() {
    var resp =
        rest.getForEntity(BASE + "/" + BOOK_HP + "/copies/" + UUID.randomUUID(), String.class);
    assertEquals(404, resp.getStatusCode().value());
  }

  @Test
  void stockIncrement() {
    var req = new com.example.demo.dto.StockRequest(3);
    var resp =
        rest.postForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/increment", req, String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("\"copyId\""));
  }

  @Test
  void stockDecrement_insufficient() {
    var req = new com.example.demo.dto.StockRequest(999);
    var resp =
        rest.postForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/decrement", req, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  @Test
  void stockHistory() {
    var resp =
        rest.getForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/history", String.class);
    assertEquals(200, resp.getStatusCode().value());
  }

  @Test
  void createSale_success() {
    var items =
        Set.of(
            new com.example.demo.dto.SaleItemInput(COPY_HP_P.toString(), 2),
            new com.example.demo.dto.SaleItemInput(COPY_LOTR_B.toString(), 1));
    var input = new com.example.demo.dto.SaleInput(CUST_JEAN.toString(), items);
    var resp = rest.postForEntity("/sales", input, String.class);
    assertEquals(201, resp.getStatusCode().value());
  }

  @Test
  void createSale_insufficientStock() {
    var items = Set.of(new com.example.demo.dto.SaleItemInput(COPY_HP_P.toString(), 9999));
    var input = new com.example.demo.dto.SaleInput(CUST_JEAN.toString(), items);
    var resp = rest.postForEntity("/sales", input, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  @Test
  void revenue_monthly() {
    var resp =
        rest.getForEntity("/book-copies/revenues?period=monthly&year=2026&month=7", String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("MONTHLY"));
  }

  @Test
  void revenue_yearly() {
    var resp = rest.getForEntity("/book-copies/revenues?period=yearly&year=2026", String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("YEARLY"));
  }

  // ---------- Book creation ----------

  @Test
  void createBook() {
    var input =
        new com.example.demo.dto.BookInput("Test Book", 5.0f, 12.0f, null, null, "TestGenre");
    var resp = rest.postForEntity("/books", input, String.class);
    assertEquals(201, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("Test Book"));
  }

  @Test
  void createBook_withCategoryAndAuthors() {
    var input =
        com.example.demo.dto.BookInput.builder()
            .title("Nouveau Roman")
            .purchasePrice(8.0f)
            .sellingPrice(15.0f)
            .categoryId(CAT_MEDIUM.toString())
            .authorIds(Set.of(AUTH_ROW.toString()))
            .genre("Fantasy")
            .build();
    var resp = rest.postForEntity("/books", input, String.class);
    assertEquals(201, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("Nouveau Roman"));
  }

  @Test
  void createBook_missingTitle() {
    var input = new com.example.demo.dto.BookInput("", 5.0f, 12.0f, null, null, null);
    var resp = rest.postForEntity("/books", input, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  @Test
  void createBook_missingPrices() {
    var input = new com.example.demo.dto.BookInput("No Price", null, null, null, null, null);
    var resp = rest.postForEntity("/books", input, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  @Test
  void createBook_badCategory() {
    var input =
        new com.example.demo.dto.BookInput(
            "Bad Cat", 5.0f, 12.0f, UUID.randomUUID().toString(), null, null);
    var resp = rest.postForEntity("/books", input, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  // ---------- ISBN lookup ----------

  @Test
  void isbnLookup_notFound() {
    var resp = rest.getForEntity("/books/lookup/this-is-not-a-real-isbn", String.class);
    assertEquals(404, resp.getStatusCode().value());
  }

  // ---------- Stock decrement success ----------

  @Test
  void stockDecrement_success() {
    var req = new com.example.demo.dto.StockRequest(2);
    var resp =
        rest.postForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/decrement", req, String.class);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody().contains("\"copyId\""));
  }

  @Test
  void stockDecrement_zeroQuantity() {
    var req = new com.example.demo.dto.StockRequest(0);
    var resp =
        rest.postForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/decrement", req, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }

  @Test
  void stockIncrement_zeroQuantity() {
    var req = new com.example.demo.dto.StockRequest(0);
    var resp =
        rest.postForEntity(
            BASE + "/" + BOOK_HP + "/copies/" + COPY_HP_P + "/stock/increment", req, String.class);
    assertEquals(400, resp.getStatusCode().value());
  }
}
