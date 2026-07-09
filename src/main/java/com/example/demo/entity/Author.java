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
public class Author {

  @Id private UUID idAuthor;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String firstName;
}
