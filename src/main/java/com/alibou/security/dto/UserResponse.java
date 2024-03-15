package com.alibou.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        @JsonProperty("first_name")
        String firstname,
        @JsonProperty("last_name")
        String lastname,
        String email


)
{}
