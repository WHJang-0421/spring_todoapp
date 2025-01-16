package com.example.todoapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import com.example.todoapp.services.AccountService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        @Autowired
        private AccountService accountService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // restore old behavior, returning csrf token in first get request
                CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                requestHandler.setCsrfRequestAttributeName(null);
                http.csrf(csrf -> csrf
                                .csrfTokenRequestHandler(requestHandler)
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
                http.authorizeHttpRequests(requests -> requests
                                .requestMatchers("/", "/api/register", "/assets/**", "/vite.svg", "/error")
                                .permitAll()
                                .anyRequest().hasRole("USER"));
                http.formLogin(form -> form
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .loginPage("/").permitAll()
                                .loginProcessingUrl("/api/login").permitAll()
                                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value()))
                                .failureHandler(new SimpleUrlAuthenticationFailureHandler()));
                http.oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(accountService)));
                http.exceptionHandling(exception -> exception
                                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()));
                return http.build();
        }

        // @Bean
        // UrlBasedCorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        // UrlBasedCorsConfigurationSource source = new
        // UrlBasedCorsConfigurationSource();
        // source.registerCorsConfiguration("/**", configuration);
        // return source;
        // }
}
