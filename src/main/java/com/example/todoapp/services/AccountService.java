package com.example.todoapp.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.todoapp.domain.Account;
import com.example.todoapp.dto.AccountDto;
import com.example.todoapp.repositories.AccountRepository;
import com.example.todoapp.security.adapters.OidcUserAccount;
import com.example.todoapp.security.adapters.UserAccount;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService, OAuth2UserService<OidcUserRequest, OidcUser> {
    private final AccountRepository accountRepository;
    private final OidcUserService delegate = new OidcUserService();
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation for loading a user
        OidcUser oidcUser = delegate.loadUser(userRequest);

        ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();

        // find account from db, save if not found
        Account account = accountRepository.findBySub(oidcUser.getAttribute(userNameAttributeName));
        if (account == null) {
            account = accountRepository.save(Account.builder()
                    .isOidc(true)
                    .sub(oidcUser.getAttribute(userNameAttributeName))
                    .oidcPreferredName(oidcUser.getAttribute("name"))
                    .role("USER")
                    .build());
        }

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole()));

        OidcUserAccount oidcUserAccount;
        if (StringUtils.hasText(userNameAttributeName)) {
            oidcUserAccount = new OidcUserAccount(mappedAuthorities, oidcUser.getIdToken(),
                    oidcUser.getUserInfo(), userNameAttributeName, account);
        } else {
            oidcUserAccount = new OidcUserAccount(mappedAuthorities, oidcUser.getIdToken(),
                    oidcUser.getUserInfo(), account);
        }
        return oidcUserAccount;
    }

    public static Account getAccountFromPrincipal(UserAccount userAccount, OidcUserAccount oidcUserAccount) {
        Account account;
        if (userAccount == null) {
            account = oidcUserAccount.getAccount();
        } else {
            account = userAccount.getAccount();
        }
        return account;
    }

    public boolean registerWithUsername(AccountDto accountDto) {
        Account account = accountRepository.findByUsername(accountDto.getUsername());
        if (account == null) {
            accountRepository.save(Account.builder()
                    .isOidc(false)
                    .username(accountDto.getUsername())
                    .password(passwordEncoder.encode(accountDto.getPassword()))
                    .role("USER")
                    .build());
            return true;
        }

        return false;
    }
}
