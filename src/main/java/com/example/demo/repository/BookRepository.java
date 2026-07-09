package com.example.demo.repository;

import com.example.demo.entity.Book;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, UUID> {
  List<Book> findByGenreIgnoreCase(String genre);
}
