package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IsbnBookDetail {
  private String isbn;
  private String title;
  private List<String> authors;
  private String publisher;
  private String publishDate;
  private Integer pageCount;
  private List<String> subjects;
  private String source;
}
