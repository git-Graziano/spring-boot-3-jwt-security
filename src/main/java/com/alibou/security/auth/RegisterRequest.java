package com.alibou.security.auth;

import com.alibou.security.user.Role;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record RegisterRequest (
    @NotBlank
    @JsonProperty("first_name")
    String firstname,

    @NotBlank
    @JsonProperty("last_name")
    String lastname,
    @Email
    String email,
    @NotBlank
    String password,
    Role role
){}
