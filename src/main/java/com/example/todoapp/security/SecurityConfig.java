package com.example.todoapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.todoapp.services.AccountService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/register").permitAll()
                .anyRequest().hasRole("USER"));
        http.formLogin(Customizer.withDefaults());
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(accountService)));
        return http.build();
    }
}
