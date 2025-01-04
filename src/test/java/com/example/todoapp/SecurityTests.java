package com.example.todoapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.todoapp.domain.Account;
import com.example.todoapp.security.OidcUserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.HashMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.hamcrest.Matchers;

@ContextConfiguration(classes = TodoappApplication.class)
@AutoConfigureMockMvc
@SpringBootTest
class SecurityTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@Transactional
	public void registerAndFormLogin() throws Exception {
		Map<String, String> input = new HashMap<>();
		input.put("username", "testuser");
		input.put("password", "abc");

		mockMvc.perform(post("/register")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(input)))
				.andExpect(status().isOk())
				.andDo(print());

		mockMvc.perform(formLogin("/login").user("testuser").password("abc"))
				.andExpect(authenticated());
	}

	@Test
	@Transactional
	public void oauth2LoginSuccess() throws Exception {

		OidcUserAccount userAccount = new OidcUserAccount(
				AuthorityUtils.createAuthorityList("ROLE_USER"),
				OidcIdToken
						.withTokenValue("id-token")
						.claim("sub", "test_user_sub")
						.build(),
				new OidcUserInfo(Map.of(
						"sub", "test_user_sub",
						"name", "testuser2")),
				Account.builder()
						.oidcPreferredName("testuser2")
						.isOidc(true)
						.role("USER")
						.sub("test_user_sub")
						.build());
		mockMvc.perform(get("/user").with(oidcLogin()
				.oidcUser(userAccount)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", Matchers.is("testuser2")));
	}
}
