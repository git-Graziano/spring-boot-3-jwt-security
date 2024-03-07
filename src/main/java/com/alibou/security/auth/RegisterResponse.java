package com.alibou.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponse(
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        String email
)
{}
