package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  @Id private UUID idCategory;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CategoryName categoryName;

  public enum CategoryName {
    LARGE,
    MEDIUM,
    SMALL
  }
}
