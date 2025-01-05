package com.example.todoapp.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskDto {
    private Long id;
    private String name;
    private LocalDate due;
    private boolean finished;
    private String ownerName;
}
