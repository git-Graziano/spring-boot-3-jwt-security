package com.alibou.security.mapper;

import com.alibou.security.dto.JwtToken;
import com.alibou.security.dto.AuthorityEnum;
import com.alibou.security.dto.UserDto;
import com.alibou.security.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toDto(User entity) {

        return UserDto.builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .email(entity.getEmail())
                .avatar(getAvatar(entity.getFirstname(), entity.getLastname()))
                .authorities(entity.getAuthorities().stream()
                        .map(a -> AuthorityEnum.valueOf(a.getAuthority()))
                        .collect(Collectors.toList()))
                .isAdmin(checkIsAdmin(entity.getAuthorities()))
                .build();
    }

    public static UserDto toDto(User entity, JwtToken accessToken, JwtToken refreshToken) {

        var user = toDto(entity);
        user.setAccessToken(accessToken.getToken());
        user.setRefreshToken(refreshToken.getToken());
        return user;
    }

    private static String getAvatar(String firstName, String lastName) {
        return firstName.substring(0, 1).toUpperCase() +
                lastName.substring(0, 1).toUpperCase();
    }

    private static Boolean checkIsAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(AuthorityEnum.ADMIN.name().toUpperCase()));
    }
}
