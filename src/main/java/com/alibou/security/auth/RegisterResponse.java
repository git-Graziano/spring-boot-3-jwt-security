package com.alibou.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponse(
        @JsonProperty("first_name")
        String firstname,
        @JsonProperty("last_name")
        String lastname,
        String email
)
{}
