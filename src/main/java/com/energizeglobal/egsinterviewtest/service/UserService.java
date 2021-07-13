package com.energizeglobal.egsinterviewtest.service;

import com.energizeglobal.egsinterviewtest.domain.Authority;
import com.energizeglobal.egsinterviewtest.domain.User;
import com.energizeglobal.egsinterviewtest.repository.AuthorityRepository;
import com.energizeglobal.egsinterviewtest.repository.UserRepository;
import com.energizeglobal.egsinterviewtest.security.AuthoritiesConstants;
import com.energizeglobal.egsinterviewtest.security.SecurityUtils;
import com.energizeglobal.egsinterviewtest.service.dto.AdminUserDTO;
import com.energizeglobal.egsinterviewtest.service.dto.UserDTO;
import com.energizeglobal.egsinterviewtest.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorityRepository authorityRepository,
                       CacheManager cacheManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    public Optional<User> activateRegistration(String token) {

        log.debug("Activating user for activation token {}", token);

        return userRepository
                .findOneByActivationToken(token)
                .map(
                        user -> {
                            // activate given user for the registration token.
                            user.setIsActivated(true);
                            user.setActivationToken(null);
                            this.clearUserCaches(user);
                            log.debug("Activated user: {}", user);
                            return user;
                        }
                );
    }

    public User registerUser(AdminUserDTO userDTO, String password) {

        userRepository
                .findOneByUsername(userDTO.getUsername().toLowerCase())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new UsernameAlreadyUsedException();
                            }
                        }
                );

        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new EmailAlreadyUsedException();
                            }
                        }
                );

        User newUser = new User();

        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setUsername(userDTO.getUsername().toLowerCase());

        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());

        if (userDTO.getEmail() != null) {

            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }

        newUser.setAvatarUrl(userDTO.getAvatarUrl());

        // new user is not active
        newUser.setIsActivated(false);

        // new user gets registration key
        newUser.setActivationToken(RandomUtil.generateActivationToken());

        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);

        userRepository.save(newUser);

        this.clearUserCaches(newUser);

        log.debug("Created Information for User: {}", newUser);

        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {

        if (existingUser.getIsActivated()) {

            return false;
        }

        userRepository.delete(existingUser);
        userRepository.flush();

        this.clearUserCaches(existingUser);

        return true;
    }

    public User createUser(AdminUserDTO userDTO) {

        User user = new User();

        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        if (userDTO.getEmail() != null) {

            user.setEmail(userDTO.getEmail().toLowerCase());
        }

        user.setAvatarUrl(userDTO.getAvatarUrl());

        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());

        user.setPassword(encryptedPassword);

        user.setIsActivated(true);

        if (userDTO.getAuthorities() != null) {

            Set<Authority> authorities = userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            user.setAuthorities(authorities);
        }

        userRepository.save(user);

        this.clearUserCaches(user);

        log.debug("Created Information for User: {}", user);

        return user;
    }

    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {

        return Optional
                .of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(
                        user -> {
                            this.clearUserCaches(user);

                            user.setUsername(userDTO.getUsername().toLowerCase());
                            user.setFirstName(userDTO.getFirstName());
                            user.setLastName(userDTO.getLastName());

                            if (userDTO.getEmail() != null) {

                                user.setEmail(userDTO.getEmail().toLowerCase());
                            }

                            user.setAvatarUrl(userDTO.getAvatarUrl());
                            user.setIsActivated(userDTO.getIsActivated());

                            Set<Authority> managedAuthorities = user.getAuthorities();

                            managedAuthorities.clear();

                            userDTO
                                    .getAuthorities()
                                    .stream()
                                    .map(authorityRepository::findById)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .forEach(managedAuthorities::add);

                            this.clearUserCaches(user);

                            log.debug("Changed Information for User: {}", user);

                            return user;
                        }
                )
                .map(AdminUserDTO::new);
    }

    public void deleteUser(String username) {

        userRepository
                .findOneByUsername(username)
                .ifPresent(
                        user -> {

                            userRepository.delete(user);

                            this.clearUserCaches(user);

                            log.debug("Deleted User: {}", user);
                        }
                );
    }

    public void updateUser(String firstName, String lastName, String email, String imageUrl) {

        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsername)
                .ifPresent(
                        user -> {

                            user.setFirstName(firstName);
                            user.setLastName(lastName);

                            if (email != null) {

                                user.setEmail(email.toLowerCase());
                            }


                            user.setAvatarUrl(imageUrl);

                            this.clearUserCaches(user);

                            log.debug("Changed Information for User: {}", user);
                        }
                );
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {

        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsername)
                .ifPresent(

                        user -> {

                            String currentEncryptedPassword = user.getPassword();

                            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {

                                throw new InvalidPasswordException();
                            }

                            String encryptedPassword = passwordEncoder.encode(newPassword);
                            user.setPassword(encryptedPassword);

                            this.clearUserCaches(user);

                            log.debug("Changed password for User: {}", user);
                        }
                );
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable)
    {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {

        return userRepository.findAllByIdNotNullAndIsActivatedTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String username) {

        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {

        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {

        userRepository
                .findAllByIsActivatedFalseAndActivationTokenIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
                .forEach(
                        user -> {

                            log.debug("Deleting not activated user {}", user.getUsername());

                            userRepository.delete(user);

                            this.clearUserCaches(user);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<String> getAuthorities() {

        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    private void clearUserCaches(User user) {

        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_USERNAME_CACHE)).evict(user.getUsername());

        if (user.getEmail() != null) {

            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }
}
