package com.energizeglobal.egsinterviewtest.web.rest;

import com.energizeglobal.egsinterviewtest.domain.User;
import com.energizeglobal.egsinterviewtest.repository.UserRepository;
import com.energizeglobal.egsinterviewtest.security.SecurityUtils;
import com.energizeglobal.egsinterviewtest.service.MailService;
import com.energizeglobal.egsinterviewtest.service.UserService;
import com.energizeglobal.egsinterviewtest.service.dto.AdminUserDTO;
import com.energizeglobal.egsinterviewtest.web.rest.errors.EmailAlreadyUsedException;
import com.energizeglobal.egsinterviewtest.web.rest.errors.InvalidPasswordException;
import com.energizeglobal.egsinterviewtest.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {

            throw new InvalidPasswordException();
        }

        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());

//        mailService.sendActivationEmail(user);
    }

    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {

        Optional<User> user = userService.activateRegistration(key);

        if (user.isEmpty()) {

            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {

        log.debug("REST request to check if the current user is authenticated");

        return request.getRemoteUser();
    }

    @GetMapping("/account")
    public AdminUserDTO getAccount() {

        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {

        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());

        if (existingUser.isPresent() && (!existingUser.get().getUsername().equalsIgnoreCase(userLogin))) {

            throw new EmailAlreadyUsedException();
        }

        Optional<User> user = userRepository.findOneByUsername(userLogin);

        if (user.isEmpty()) {

            throw new AccountResourceException("User could not be found");
        }

        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getAvatarUrl()
        );
    }

    private static boolean isPasswordLengthInvalid(String password) {

        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
