package com.example.demo.dto;

import java.util.UUID;
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
public class CustomerDetail {
  private UUID idCustomer;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String email;
}
