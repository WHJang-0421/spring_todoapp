package com.example.todoapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account {
    @Id
    @GeneratedValue
    private long id;

    private boolean isOidc;

    // for username-password authentication
    @Column(unique = true)
    private String username;

    @JsonIgnore
    @ToString.Exclude
    private String password;

    // for oidc
    @Column(unique = true)
    private String sub;

    private String oidcPreferredName;

    // common
    private String role;

    public String getDisplayName() {
        if (isOidc) {
            return oidcPreferredName;
        } else {
            return username;
        }
    }
}