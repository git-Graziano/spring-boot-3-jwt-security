package com.alibou.security.service.impl;

import com.alibou.security.dto.AuthenticationRequest;
import com.alibou.security.dto.RegisterRequest;
import com.alibou.security.dto.RegisterResponse;
import com.alibou.security.exception.AuthorityNotFoundException;
import com.alibou.security.exception.RefreshTokenExpiredException;
import com.alibou.security.exception.UserAlreadyRegisteredException;
import com.alibou.security.email.MailService;
import com.alibou.security.dto.JwtToken;
import com.alibou.security.mapper.UserMapper;
import com.alibou.security.exception.UserNotFoundException;
import com.alibou.security.model.Token;
import com.alibou.security.dto.UserDto;
import com.alibou.security.repository.TokenRepository;
import com.alibou.security.model.TokenType;
import com.alibou.security.model.User;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.repository.AuthorityRepository;
import com.alibou.security.model.PasswordRecovery;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
@Log
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {
  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final MailService mailService;

  public RegisterResponse register(RegisterRequest request)  {

    var role = authorityRepository.findByName(request.role()).orElseThrow(() -> new AuthorityNotFoundException("Not found"));

    // check for email duplication
    var isUserIsAlreadyRegistered = userRepository.findByEmail(request.email());
    if(isUserIsAlreadyRegistered.isPresent()) {
      var t = isUserIsAlreadyRegistered.get().getAuthorities().size();
            throw new UserAlreadyRegisteredException("User already registered");
    }
    var user = User.builder()
        .firstname(request.firstname())
        .lastname(request.lastname())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .authorities(Set.of(role))
        .build();
    var savedUser = userRepository.save(user);

    return new RegisterResponse(
            savedUser.getFirstname(),
            savedUser.getLastname(),
            savedUser.getEmail());
  }

  public UserDto authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );
    var user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid credentials"));
    var accessToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
//    revokeAllUserTokens(user);
    saveUserToken(user, accessToken, TokenType.ACCESS);
    saveUserToken(user, refreshToken, TokenType.REFRESH);

    // Get avatar
    String avatar = user.getFirstname().substring(0, 1).toUpperCase() +
            user.getLastname().substring(0, 1).toUpperCase();

    return UserMapper.toDto(user, accessToken, refreshToken);
  }

  private void saveUserToken(User user, JwtToken token, TokenType type) {

    // get user token if exists
    var optToken = tokenRepository.findByUserAndType(user, type);
    Token tokenEntity;
    if(optToken.isEmpty()) {

      // create new token
      tokenEntity = Token.builder()
              .user(user)
              .type(type)
              .token(token.getToken())
              .issuedAt(token.getIssuedAt())
              .expiredAt(token.getExpiredAt())
              .allowed(true)
              .build();
    }
    else {

      //TODO: check if the token is allowed

      // update existing token
      tokenEntity = optToken.get();
      tokenEntity.setToken(token.getToken());
      tokenEntity.setIssuedAt(token.getIssuedAt());
      tokenEntity.setExpiredAt(token.getExpiredAt());
      tokenEntity.setAllowed(true);
    }
    tokenRepository.save(tokenEntity);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.userRepository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        //saveUserToken(user, accessToken);
        var authResponse = UserDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public UserDto refreshToken(
          String refreshToken
  ) {
    UserDto authenticationResponse = new UserDto();
    final String userEmail;
    // se è scaduto anche il refresh token invio 403 forbidden invece di 401 unauthorized
    try {
      userEmail = jwtService.extractUsername(refreshToken);
    }
    catch (ExpiredJwtException ex) {
      log.warning("Error extracting user from JWT: token expired");
      throw new RefreshTokenExpiredException("Refresh token expired");
    }
    if (userEmail != null) {
      var user = this.userRepository.findByEmail(userEmail)
              .orElseThrow(() -> new UserNotFoundException("User not found"));
      if (jwtService.isTokenValid(refreshToken, user)) {
        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        //revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken, TokenType.ACCESS);
        saveUserToken(user, newRefreshToken, TokenType.REFRESH);
        authenticationResponse = UserDto.builder()
                .accessToken(newAccessToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .build();
      }
    }
    return authenticationResponse;
  }

  public void forgot(String email, String originUrl) {
    var token = UUID.randomUUID().toString().replace("-", "");
    var user = userRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("User not found"));

    var passwordRecovery = PasswordRecovery.builder()
            .token(token)
            .user(user)
            .build();

//    user.getPasswordRecovery().add(passwordRecovery);
    userRepository.save(user);
    mailService.sendForgotMessage(email, token, originUrl);
  }

  public void logout(String token) {

    try {
      final var userEmail = jwtService.extractUsername(token);
      if (userEmail != null) {
        var user = this.userRepository.findByEmail(userEmail)
                .orElseThrow();
        var accesstoken = tokenRepository.findByUserAndType(user, TokenType.ACCESS);
        if (accesstoken.isPresent()) {
          var entity = accesstoken.get();
          entity.setAllowed(false);
          tokenRepository.save(entity);
        }

        var refreshToken = tokenRepository.findByUserAndType(user, TokenType.REFRESH);
        if (refreshToken.isPresent()) {
          var entity = refreshToken.get();
          entity.setAllowed(false);
          tokenRepository.save(entity);
        }
        SecurityContextHolder.clearContext();
      }
    }
    catch (RuntimeException ex) {
      // refresh token expired or malformed. Try to disallow all expired tokens
      log.warning("refresh token expired or malformed");
    }

    //TODO: should be disallowed all the tokens with expiredAt after now
  }

}

