package com.energizeglobal.egsinterviewtest.web.rest;

import com.energizeglobal.egsinterviewtest.config.Constants;
import com.energizeglobal.egsinterviewtest.domain.User;
import com.energizeglobal.egsinterviewtest.repository.UserRepository;
import com.energizeglobal.egsinterviewtest.security.AuthoritiesConstants;
import com.energizeglobal.egsinterviewtest.service.MailService;
import com.energizeglobal.egsinterviewtest.service.UserService;
import com.energizeglobal.egsinterviewtest.service.dto.AdminUserDTO;
import com.energizeglobal.egsinterviewtest.web.rest.errors.BadRequestAlertException;
import com.energizeglobal.egsinterviewtest.web.rest.errors.EmailAlreadyUsedException;
import com.energizeglobal.egsinterviewtest.web.rest.errors.UsernameAlreadyUsedException;
import com.energizeglobal.egsinterviewtest.web.util.HeaderUtil;
import com.energizeglobal.egsinterviewtest.web.util.PaginationUtil;
import com.energizeglobal.egsinterviewtest.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class UserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "username", "firstName", "lastName", "email", "activated", "langKey")
    );

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${app.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final MailService mailService;

    public UserResource(UserService userService, UserRepository userRepository, MailService mailService) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {

        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {

            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByUsername(userDTO.getUsername().toLowerCase()).isPresent()) {

            throw new UsernameAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
//            mailService.sendCreationEmail(newUser);
            return ResponseEntity
                .created(new URI("/api/admin/users/" + newUser.getUsername()))
                .headers(HeaderUtil
                        .createAlert(applicationName, "userManagement.created", newUser.getUsername()))
                .body(newUser);
        }
    }

    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> updateUser(@Valid @RequestBody AdminUserDTO userDTO) {

        log.debug("REST request to update User : {}", userDTO);

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {

            throw new EmailAlreadyUsedException();
        }

        existingUser = userRepository.findOneByUsername(userDTO.getUsername().toLowerCase());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {

            throw new UsernameAlreadyUsedException();
        }

        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);

        return ResponseUtil.wrapOrNotFound(
            updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getUsername())
        );
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(Pageable pageable) {

        log.debug("REST request to get all User for an admin");

        if (!onlyContainsAllowedProperties(pageable)) {

            return ResponseEntity.badRequest().build();
        }

        final Page<AdminUserDTO> page = userService.getAllManagedUsers(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder
                .fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty)
                .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    @GetMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> getUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {

        log.debug("REST request to get User : {}", login);

        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(AdminUserDTO::new));
    }

    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {

        log.debug("REST request to delete User: {}", login);

        userService.deleteUser(login);

        return ResponseEntity.noContent().headers(HeaderUtil
                .createAlert(applicationName, "userManagement.deleted", login)).build();
    }
}
