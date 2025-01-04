package com.example.todoapp;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.todoapp.domain.Account;
import com.example.todoapp.security.adapters.OidcUserAccount;
import com.example.todoapp.security.adapters.UserAccount;
import com.example.todoapp.services.AccountService;
import com.example.todoapp.dto.AccountDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@SpringBootApplication
public class TodoappApplication {
	@Autowired
	private AccountService accountService;

	@GetMapping("/user")
	public Map<String, Object> user(@AuthenticationPrincipal UserAccount userAccount,
			@AuthenticationPrincipal(errorOnInvalidType = true) OidcUserAccount oidcUserAccount) {
		Account account;
		if (userAccount == null) {
			account = oidcUserAccount.getAccount();
		} else {
			account = userAccount.getAccount();
		}

		return Collections.singletonMap("username",
				account.getDisplayName());
	}

	@PostMapping("/register")
	public String register(@RequestBody AccountDto accountDto) throws Exception {
		if (accountService.registerWithUsername(accountDto)) {
			return "Register Success";
		}

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
	}

	public static void main(String[] args) {
		SpringApplication.run(TodoappApplication.class, args);
	}
}
