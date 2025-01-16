package com.example.todoapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.todoapp.domain.Account;
import com.example.todoapp.dto.TaskDto;
import com.example.todoapp.security.adapters.OidcUserAccount;
import com.example.todoapp.security.adapters.UserAccount;
import com.example.todoapp.services.AccountService;
import com.example.todoapp.services.TaskService;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/api/task/{id}")
    public TaskDto findTask(@PathVariable Long id) throws Exception {
        return taskService
                .findTask(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Task with id " + id + " doesn't exist"));
    }

    @PutMapping("/api/task/{id}")
    public String modifyTask(@PathVariable Long id, @RequestBody TaskDto taskDto) throws Exception {
        taskService.updateTaskById(id, taskDto);
        return "Modify item of id " + id + ": success";
    }

    @DeleteMapping("/api/task/{id}")
    public String deleteTask(@PathVariable Long id) throws Exception {
        taskService.deleteTask(id);
        return "Delete Success";
    }

    @GetMapping("/api/task")
    public List<TaskDto> findAllTasks(@AuthenticationPrincipal UserAccount userAccount,
            @AuthenticationPrincipal OidcUserAccount oidcUserAccount) {
        Account account = AccountService.getAccountFromPrincipal(userAccount, oidcUserAccount);
        return taskService.findAllTasks(account);
    }

    @PostMapping("/api/task")
    public TaskDto addTask(@AuthenticationPrincipal UserAccount userAccount,
            @AuthenticationPrincipal OidcUserAccount oidcUserAccount, @RequestBody TaskDto taskDto) {
        Account account = AccountService.getAccountFromPrincipal(userAccount, oidcUserAccount);
        return taskService.saveTask(taskDto, account);
    }
}
