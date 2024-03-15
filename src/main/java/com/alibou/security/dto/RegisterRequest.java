package com.alibou.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
    @NotBlank(message = "Nome non valido")
    @JsonProperty("firstname")
    String firstname,

    @NotBlank(message = "Cognome non valido")
    @JsonProperty("lastname")
    String lastname,
    @Email(message = "Indirizzo di posta elettronica non valido")
    String email,
    @NotBlank(message = "Password non valida")
    String password,
    String role
){}
