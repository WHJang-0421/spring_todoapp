package com.example.todoapp.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class AccountDto {
    private String username;
    private String password;
}
