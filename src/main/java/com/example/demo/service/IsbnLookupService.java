package com.example.demo.service;

import com.example.demo.dto.IsbnBookDetail;
import com.example.demo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IsbnLookupService {

  private static final String OPEN_LIBRARY_URL =
      "https://openlibrary.org/api/books?bibkeys=ISBN:%s&format=json&jscmd=data";
  private static final String GOOGLE_BOOKS_URL =
      "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";

  private final HttpClient httpClient =
      HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(5))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build();

  private final ObjectMapper mapper = new ObjectMapper();

  public IsbnBookDetail lookup(String isbn) {
    if (isbn == null || isbn.isBlank()) {
      throw new ResourceNotFoundException("ISBN is empty");
    }

    IsbnBookDetail result = tryOpenLibrary(isbn);
    if (result != null) return result;

    result = tryGoogleBooks(isbn);
    if (result != null) return result;

    throw new ResourceNotFoundException("Book not found for ISBN: " + isbn);
  }

  private IsbnBookDetail tryOpenLibrary(String isbn) {
    try {
      String url = String.format(OPEN_LIBRARY_URL, isbn);
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .timeout(Duration.ofSeconds(5))
              .GET()
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) return null;

      JsonNode root = mapper.readTree(response.body());
      String key = "ISBN:" + isbn;
      JsonNode bookNode = root.get(key);

      if (bookNode == null || bookNode.isNull() || bookNode.isEmpty()) return null;

      String title = bookNode.has("title") ? bookNode.get("title").asText() : null;
      if (title == null) return null;

      List<String> authors = new ArrayList<>();
      JsonNode authorsNode = bookNode.get("authors");
      if (authorsNode != null && authorsNode.isArray()) {
        for (JsonNode author : authorsNode) {
          if (author.has("name")) {
            authors.add(author.get("name").asText());
          }
        }
      }

      String publisher = null;
      JsonNode publishersNode = bookNode.get("publishers");
      if (publishersNode != null && publishersNode.isArray() && publishersNode.size() > 0) {
        publisher = publishersNode.get(0).get("name").asText();
      }

      String publishDate =
          bookNode.has("publish_date") ? bookNode.get("publish_date").asText() : null;

      Integer pageCount =
          bookNode.has("number_of_pages") ? bookNode.get("number_of_pages").asInt() : null;

      List<String> subjects = new ArrayList<>();
      JsonNode subjectsNode = bookNode.get("subjects");
      if (subjectsNode != null && subjectsNode.isArray()) {
        for (JsonNode s : subjectsNode) {
          if (s.has("name")) subjects.add(s.get("name").asText());
        }
      }

      return IsbnBookDetail.builder()
          .isbn(isbn)
          .title(title)
          .authors(authors)
          .publisher(publisher)
          .publishDate(publishDate)
          .pageCount(pageCount)
          .subjects(subjects)
          .source("OpenLibrary")
          .build();

    } catch (Exception e) {
      return null;
    }
  }

  private IsbnBookDetail tryGoogleBooks(String isbn) {
    try {
      String encodedIsbn = URLEncoder.encode(isbn, StandardCharsets.UTF_8);
      String url = String.format(GOOGLE_BOOKS_URL, encodedIsbn);
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .timeout(Duration.ofSeconds(5))
              .header("Accept", "application/json")
              .GET()
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) return null;

      JsonNode root = mapper.readTree(response.body());
      JsonNode items = root.get("items");

      if (items == null || !items.isArray() || items.isEmpty()) return null;

      JsonNode volumeInfo = items.get(0).get("volumeInfo");
      if (volumeInfo == null) return null;

      String title = volumeInfo.has("title") ? volumeInfo.get("title").asText() : null;
      if (title == null) return null;

      List<String> authors = new ArrayList<>();
      JsonNode authorsNode = volumeInfo.get("authors");
      if (authorsNode != null && authorsNode.isArray()) {
        for (JsonNode a : authorsNode) {
          authors.add(a.asText());
        }
      }

      String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").asText() : null;

      String publishDate =
          volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").asText() : null;

      Integer pageCount = volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").asInt() : null;

      List<String> categories = new ArrayList<>();
      JsonNode catsNode = volumeInfo.get("categories");
      if (catsNode != null && catsNode.isArray()) {
        for (JsonNode c : catsNode) {
          categories.add(c.asText());
        }
      }

      return IsbnBookDetail.builder()
          .isbn(isbn)
          .title(title)
          .authors(authors)
          .publisher(publisher)
          .publishDate(publishDate)
          .pageCount(pageCount)
          .subjects(categories)
          .source("GoogleBooks")
          .build();

    } catch (Exception e) {
      return null;
    }
  }
}
