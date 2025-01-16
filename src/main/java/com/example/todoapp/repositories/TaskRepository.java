package com.example.todoapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.example.todoapp.domain.Account;
import com.example.todoapp.domain.Task;
import java.util.List;
import java.util.Optional;

@Component("taskRepository")
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(Account owner);

    // Okay this is kind of lousy
    @PostAuthorize("!returnObject.isPresent() || returnObject.get().owner.id == authentication.getPrincipal().account.id")
    @NonNull
    Optional<Task> findById(@NonNull Long id);

    @PostAuthorize("returnObject.owner.id == authentication.getPrincipal().account.id")
    @NonNull
    Task getReferenceById(@NonNull Long id);

    @PreAuthorize("@taskRepository.getReferenceById(#id).owner.id == authentication.getPrincipal().account.id")
    void deleteById(@NonNull Long id);
}