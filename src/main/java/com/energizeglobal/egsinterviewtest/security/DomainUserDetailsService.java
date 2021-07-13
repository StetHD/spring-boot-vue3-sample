package com.energizeglobal.egsinterviewtest.security;

import com.energizeglobal.egsinterviewtest.domain.User;
import com.energizeglobal.egsinterviewtest.repository.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Authenticating {}", username);

        if (new EmailValidator().isValid(username, null)) {

            return userRepository
                    .findOneWithAuthoritiesByEmailIgnoreCase(username)
                    .map(user -> createSpringSecurityUser(username, user))
                    .orElseThrow(() -> new UsernameNotFoundException(String
                            .format("User with email %s was not found in the database", username)));
        }

        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);

        return userRepository
                .findOneWithAuthoritiesByUsername(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException(String
                        .format("User %s was not found in the database", lowercaseLogin)));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseUsername, User user) {

        if (!user.getIsActivated()) {

            throw new UserNotActivatedException(String.format("User %s was not activated", lowercaseUsername));
        }

        List<GrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.
                core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
