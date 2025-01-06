package com.example.todoapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;

import com.example.todoapp.domain.Account;
import com.example.todoapp.domain.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(Account owner);

    // Okay this is kind of lousy
    @PostAuthorize("!returnObject.isPresent() || returnObject.get().owner.id == authentication.getPrincipal().account.id")
    @NonNull
    Optional<Task> findById(@NonNull Long id);
}