package com.alibou.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private List<AuthorityEnum> authorities;
    private String accessToken;
    private String refreshToken;
    private String avatar;
    private Boolean isAdmin;

}
