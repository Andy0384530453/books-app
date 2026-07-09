package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id private UUID idBook;

  @Column(nullable = false)
  private String title;

  private String genre;

  private LocalDate publicationDate;

  @Column(nullable = false)
  private Float purchasePrice;

  @Column(nullable = false)
  private Float sellingPrice;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_category")
  private Category category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "id_book"),
      inverseJoinColumns = @JoinColumn(name = "id_author"))
  @Builder.Default
  private Set<Author> authors = new HashSet<>();
}
