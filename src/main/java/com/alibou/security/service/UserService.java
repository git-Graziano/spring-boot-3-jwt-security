package com.alibou.security.service;

import com.alibou.security.model.PasswordRecovery;
import com.alibou.security.dto.UserDto;
import com.alibou.security.dto.ChangePasswordRequest;

import java.security.Principal;
import java.util.List;
import java.util.function.Predicate;

public interface UserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    void addPasswordRecovery(PasswordRecovery passwordRecovery);

    boolean removePasswordRecovery(PasswordRecovery passwordRecovery);

    boolean removePasswordReoveryIf(Predicate<? super PasswordRecovery> predicate);

    UserDto findByUserName(String username);

    List<UserDto> findAll();
}
