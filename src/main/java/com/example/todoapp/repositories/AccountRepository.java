package com.example.todoapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todoapp.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findBySub(String sub);
}
