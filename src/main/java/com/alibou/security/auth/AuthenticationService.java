package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.email.MailService;
import com.alibou.security.token.Token;
import com.alibou.security.token.TokenRepository;
import com.alibou.security.token.TokenType;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import com.alibou.security.user.AuthorityRepository;
import com.alibou.security.user.PasswordRecovery;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

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

    //TODO: check for email duplication
    var user = User.builder()
        .firstname(request.firstname())
        .lastname(request.lastname())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .roles(Set.of(role))
        .build();
    var savedUser = userRepository.save(user);
    return new RegisterResponse(
            savedUser.getFirstname(),
            savedUser.getLastname(),
            savedUser.getEmail());
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
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
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String token, TokenType type) {

    // get user token if exists
    var optToken = tokenRepository.findByUserAndType(user, type);
    Token tokenEntity;
    if(optToken.isEmpty()) {

      // create new token
      tokenEntity = Token.builder()
              .user(user)
              .type(type)
              .token(token)
              .build();
    }
    else {
      // update existing token
      tokenEntity = optToken.get();
      tokenEntity.setToken(token);
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
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public AuthenticationResponse refreshToken(
          String refreshToken,
          HttpServletRequest request
  ) {
    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    final var userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.userRepository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        //revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken, TokenType.ACCESS);
        saveUserToken(user, newRefreshToken, TokenType.REFRESH);
        authenticationResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newAccessToken)
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
}
