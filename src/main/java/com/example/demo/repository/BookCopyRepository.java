package com.example.demo.repository;

import com.example.demo.entity.BookCopy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
  List<BookCopy> findByBookIdBook(UUID bookId);
}
