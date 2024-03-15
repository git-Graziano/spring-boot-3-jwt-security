package com.alibou.security.service.impl;

import com.alibou.security.mapper.UserMapper;
import com.alibou.security.exception.UserNotFoundException;
import com.alibou.security.model.User;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.service.UserService;
import com.alibou.security.dto.ChangePasswordRequest;
import com.alibou.security.model.PasswordRecovery;
import com.alibou.security.dto.UserDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }

    @Override
    public void addPasswordRecovery(PasswordRecovery passwordRecovery) {

    }

    @Override
    public boolean removePasswordRecovery(PasswordRecovery passwordRecovery) {
        return false;
    }

    @Override
    public boolean removePasswordReoveryIf(Predicate<? super PasswordRecovery> predicate) {
        return false;
    }

    @Override
    public UserDto findByUserName(String username) {

        var userEntity = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("Utente non trovato"));
        return UserMapper.toDto(userEntity);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> entities = userRepository.findAll();
        return entities.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

    }
}
