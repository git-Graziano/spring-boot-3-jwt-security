package com.alibou.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(
        origins = "http://localhost:5173",
        exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"}
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

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

  public record AccessToken(@JsonProperty("access_token") String token){}
  @PostMapping("/authenticate")
  public ResponseEntity<AccessToken> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response
  ) {
    var auth = service.authenticate(request);
    Cookie cookie = new Cookie("refresh_token", auth.getRefreshToken());
    cookie.setMaxAge(3600);
    cookie.setHttpOnly(true);
    cookie.setPath("/api");

    response.addCookie(cookie);

    return ResponseEntity.ok(new AccessToken(auth.getAccessToken()));
  }

  public record RefreshTokenResponse(@JsonProperty("access_token") String token){}
  @PostMapping("/refresh-token")
  public RefreshTokenResponse refreshToken(
          @CookieValue("refresh_token") String refreshToken,
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {

    var auth = service.refreshToken(refreshToken, request);

    Cookie cookie = new Cookie("refresh_token", auth.getRefreshToken());
    cookie.setMaxAge(3600);
    cookie.setHttpOnly(true);
    cookie.setPath("/api");

    return new RefreshTokenResponse(auth.getAccessToken());
  }

  public record LogoutResponse(String message){};
  @PostMapping("/logout")
  public LogoutResponse logout(
          @CookieValue("refresh_token") String refreshToken,
          HttpServletResponse response
  ) {

    //TODO: service delete or expires tokens in db

    Cookie cookie = new Cookie("refresh_token", null);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);

    service.logout(refreshToken);

    response.addCookie(cookie);
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
