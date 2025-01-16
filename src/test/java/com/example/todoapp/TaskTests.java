package com.example.todoapp;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.todoapp.domain.Account;
import com.example.todoapp.repositories.AccountRepository;
import com.example.todoapp.security.adapters.OidcUserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

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

        private OidcUserAccount createAccount(String name, String sub, String token_value) {
                Account account = Account.builder()
                                .oidcPreferredName(name)
                                .isOidc(true)
                                .role("USER")
                                .sub(sub)
                                .build();
                accountRepository.save(account);
                OidcUserAccount userAccount = new OidcUserAccount(
                                AuthorityUtils.createAuthorityList("ROLE_USER"),
                                OidcIdToken
                                                .withTokenValue(token_value)
                                                .claim("sub", sub)
                                                .build(),
                                new OidcUserInfo(Map.of(
                                                "sub", sub,
                                                "name", name)),
                                account);
                return userAccount;

        }

        @Test
        @Transactional
        public void createAndViewTask() throws Exception {
                OidcUserAccount userAccount = createAccount("testuser2", "test_user_sub", "token-value");

                mockMvc.perform(
                                post("/api/task")
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount))
                                                .with(csrf())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(Map.of(
                                                                "name", "example task",
                                                                "due", "2024-12-13",
                                                                "finished", "false"))))
                                .andExpect(status().isOk());
                MvcResult result = mockMvc.perform(
                                get("/api/task")
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount)))
                                .andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andReturn();
                long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$[0].id")).longValue();
                mockMvc.perform(
                                get("/api/task/" + id)
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount)))
                                .andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.ownerName").value("testuser2"));
                mockMvc.perform(put("/api/task/" + id)
                                .with(oidcLogin()
                                                .oidcUser(userAccount))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "modified task",
                                                "due", "2024-12-13",
                                                "finished", "false"))))
                                .andExpect(status().isOk());
                mockMvc.perform(delete("/api/task/" + id)
                                .with(oidcLogin()
                                                .oidcUser(userAccount))
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @Transactional
        public void wrongIdFails() throws Exception {
                OidcUserAccount userAccount = createAccount("testuser2", "test_user_sub", "token-value");

                Map<String, String> input = Map.of(
                                "name", "example task",
                                "due", "2024-12-13",
                                "finished", "false");
                MvcResult result = mockMvc.perform(
                                post("/api/task")
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount))
                                                .with(csrf())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk())
                                .andReturn();
                long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();

                mockMvc.perform(
                                get("/api/task/" + (id + 1))
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Transactional
        public void wrongUserFails() throws Exception {
                OidcUserAccount userAccount = createAccount("testuser2", "test_user_sub", "token-value");
                Map<String, String> input = Map.of(
                                "name", "example task",
                                "due", "2024-12-13",
                                "finished", "false");
                MvcResult result = mockMvc.perform(
                                post("/api/task")
                                                .with(oidcLogin()
                                                                .oidcUser(userAccount))
                                                .with(csrf())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk())
                                .andReturn();
                long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
                OidcUserAccount wrongAccount = createAccount("wrong_user", "wrong_user_sub", "token-value-2");
                mockMvc.perform(
                                get("/api/task/" + id)
                                                .with(oidcLogin()
                                                                .oidcUser(wrongAccount)))
                                .andExpect(status().isForbidden());
                mockMvc.perform(put("/api/task/" + id)
                                .with(oidcLogin()
                                                .oidcUser(wrongAccount))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "modified task",
                                                "due", "2024-12-13",
                                                "finished", "false"))))
                                .andExpect(status().isForbidden());
                mockMvc.perform(delete("/api/task/" + id)
                                .with(oidcLogin()
                                                .oidcUser(wrongAccount))
                                .with(csrf()))
                                .andExpect(status().isForbidden());
        }
}
