package com.alibou.security.demo;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("/api/v1/demo-controller")
@Hidden
@PreAuthorize("hasAuthority('ADMIN')")
public class DemoController {


  @GetMapping
  public ResponseEntity<String> sayHello(HttpServletRequest request) {
    UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");



    log.info(String.format("Username %s", userDetails.getUsername()));
    return ResponseEntity.ok("Hello from secured endpoint");
  }

}
