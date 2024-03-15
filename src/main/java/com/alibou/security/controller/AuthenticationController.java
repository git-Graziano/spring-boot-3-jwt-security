package com.alibou.security.controller;

import com.alibou.security.dto.AuthenticationRequest;
import com.alibou.security.service.impl.AuthenticationService;
import com.alibou.security.dto.RegisterRequest;
import com.alibou.security.dto.RegisterResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(
        origins = "http://localhost:5173",
        exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"}
)
@RestController
@RequestMapping("/api/v1/auth")
@Log
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @Value("${application.security.jwt.refresh.expiration}")
  private long refreshJwtExpiration;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(
      @RequestBody @Valid RegisterRequest request
  ) {

    // exception handling
//     if(!Objects.equals(request.password(), request.lastname())){
//       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password do not match");
//    }
    return ResponseEntity.ok(service.register(request));
  }

  public record AuthenticationResponse(@JsonProperty("avatar") String avatar, @JsonProperty("is_admin") Boolean isAdmin, @JsonProperty("access_token") String token){}
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response
  ) {
    var auth = service.authenticate(request);
    Cookie cookie = new Cookie("refresh_token", auth.getRefreshToken());
    cookie.setMaxAge((int)refreshJwtExpiration);
    cookie.setHttpOnly(true);
    cookie.setPath("/");

    response.addCookie(cookie);

    return ResponseEntity.ok(new AuthenticationResponse(auth.getAvatar(), auth.getIsAdmin(), auth.getAccessToken()));
  }

  public record RefreshTokenResponse(@JsonProperty("access_token") String token){}
  @PostMapping("/refresh-token")
  public RefreshTokenResponse refreshToken(
          @CookieValue(value = "refresh_token") String refreshToken,
          HttpServletResponse response
  ) throws IOException {

    var auth = service.refreshToken(refreshToken);

    Cookie cookie = new Cookie("refresh_token", auth.getRefreshToken());
    cookie.setMaxAge((int)refreshJwtExpiration);
    cookie.setHttpOnly(true);
    cookie.setPath("/");

    response.addCookie(cookie);

    return new RefreshTokenResponse(auth.getAccessToken());
  }

  public record LogoutResponse(String message){}
  @PostMapping("/logout")
  public LogoutResponse logout(
          @CookieValue(name = "refresh_token", required = false) String refreshToken,
          HttpServletResponse response
  ) {
    log.info("logout in.");
    //TODO: service delete or expires tokens in db
    if(refreshToken != null ) {
      log.info("refresh token present");
      Cookie cookie = new Cookie("refresh_token", null);
      cookie.setMaxAge(0);
      cookie.setHttpOnly(true);
      cookie.setPath("/");

      service.logout(refreshToken);
      response.addCookie(cookie);
    }
    log.info("logout out");
    return new LogoutResponse("success");
  }

  public record ForgotRequest(String email) {}
  public record ForgotResponse(String message){}
  @PostMapping("/forgot")
  public ForgotResponse forgot(
          @RequestBody ForgotRequest forgotRequest,
          HttpServletRequest request) {
    var originUrl = request.getHeader("Origin");
    service.forgot(forgotRequest.email(), originUrl);
    return new ForgotResponse("success");

  }

}
