package com.example.todoapp.security.adapters;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import com.example.todoapp.domain.Account;

import lombok.Getter;

@Getter
public class OidcUserAccount extends DefaultOidcUser {
    private Account account;

    public OidcUserAccount(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
            OidcUserInfo userInfo, Account account) {
        super(authorities, idToken, userInfo);
        this.account = account;
    }

    public OidcUserAccount(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
            OidcUserInfo userInfo, String nameAttributeKey, Account account) {
        super(authorities, idToken, userInfo, nameAttributeKey);
        this.account = account;
    }
}
