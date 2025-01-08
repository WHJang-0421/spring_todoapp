package com.example.todoapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.todoapp.domain.Account;
import com.example.todoapp.domain.Task;
import com.example.todoapp.dto.TaskDto;
import com.example.todoapp.repositories.TaskRepository;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public Optional<TaskDto> findTask(Long id) {
        return taskRepository.findById(id).map(task -> TaskDto
                .builder()
                .id(task.getId())
                .name(task.getName())
                .due(task.getDue())
                .finished(task.isFinished())
                .ownerName(task.getOwner().getDisplayName())
                .build());
    }

    public List<TaskDto> findAllTasks(Account account) {
        return taskRepository.findByOwner(account)
                .stream()
                .map(task -> TaskDto
                        .builder()
                        .id(task.getId())
                        .name(task.getName())
                        .due(task.getDue())
                        .finished(task.isFinished())
                        .ownerName(task.getOwner().getDisplayName())
                        .build())
                .toList();
    }

    public TaskDto saveTask(TaskDto taskDto, Account account) {
        Task task = taskRepository.save(Task.builder()
                .name(taskDto.getName())
                .due(taskDto.getDue())
                .finished(taskDto.isFinished())
                .owner(account)
                .build());
        taskDto.setId(task.getId());
        return taskDto;
    }
}
