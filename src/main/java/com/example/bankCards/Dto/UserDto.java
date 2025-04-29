package com.example.bankCards.Dto;

import lombok.Data;
import lombok.Getter;

@Data
public class UserDto {
    @Getter
    String email;
    @Getter
    String password;
    @Getter
    String role;
}
