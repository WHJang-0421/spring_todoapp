package com.example.todoapp;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

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
import com.example.todoapp.repositories.AccountRepository;
import com.example.todoapp.security.adapters.OidcUserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = TodoappApplication.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TaskTests {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void createAndViewTask() throws Exception {
        Account account = Account.builder()
                .oidcPreferredName("testuser2")
                .isOidc(true)
                .role("USER")
                .sub("test_user_sub")
                .build();
        accountRepository.save(account);

        OidcUserAccount userAccount = new OidcUserAccount(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                OidcIdToken
                        .withTokenValue("id-token")
                        .claim("sub", "test_user_sub")
                        .build(),
                new OidcUserInfo(Map.of(
                        "sub", "test_user_sub",
                        "name", "testuser2")),
                account);

        Map<String, String> input = Map.of(
                "name", "example task",
                "due", "2024-12-13",
                "finished", "false");
        mockMvc.perform(
                post("/task")
                        .with(oidcLogin()
                                .oidcUser(userAccount))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/task")
                        .with(oidcLogin()
                                .oidcUser(userAccount)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
