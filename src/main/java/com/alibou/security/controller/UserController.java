package com.alibou.security.controller;

import com.alibou.security.service.UserService;
import com.alibou.security.dto.AuthorityEnum;
import com.alibou.security.dto.ChangePasswordRequest;
import com.alibou.security.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
@Log
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping("/users")
    @GetMapping
    public ResponseEntity<List<UserDto>> users() {
        log.info("UserController::users in");
        return ResponseEntity.ok(userService.findAll());
    }

    public record UserResponse(@JsonProperty("userName") String userName, @JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName, String email, @JsonProperty("avatar") String avatar, @JsonProperty("roles") List<AuthorityEnum> roles){}

    @RequestMapping("/user")
    @GetMapping
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request) {
        log.info("UserController::users in");
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
        var user = userService.findByUserName(userDetails.getUsername());

        log.info(String.format("Username %s", userDetails.getUsername()));
        return ResponseEntity.ok(new UserResponse(user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getAvatar(),
                user.getAuthorities()));
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
