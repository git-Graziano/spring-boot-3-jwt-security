package com.alibou.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterResponse(
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        String email
)
{}
