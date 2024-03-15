package com.alibou.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class JwtToken {

    private String token;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}
