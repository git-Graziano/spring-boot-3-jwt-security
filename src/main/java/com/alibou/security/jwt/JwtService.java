package com.alibou.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${application.security.jwt.access.secret-key}")
  private String accessSecretKey;
  @Value("${application.security.jwt.access.expiration}")
  private long accessJwtExpiration;
  @Value("${application.security.jwt.refresh.secret-key}")
  private String refreshSecretKey;
  @Value("${application.security.jwt.refresh.expiration}")
  private long refreshJwtExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public JwtToken generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  private JwtToken generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, accessSecretKey, accessJwtExpiration);
  }

  public JwtToken generateRefreshToken(
      UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, accessSecretKey, refreshJwtExpiration);
  }

  private JwtToken buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          String secretKey,
          long expiration
  ) {

    var iatLocalDateTime = LocalDateTime.now();
    var expLocalDateTime = LocalDateTime.now().plusSeconds(expiration);
    Date iatDate = Date.from(iatLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
    Date expDate = Date.from(expLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

    //original
//          .setIssuedAt(new Date(System.currentTimeMillis()))
//          .setExpiration(new Date(System.currentTimeMillis() + expiration))

    String token = Jwts
          .builder()
          .setClaims(extraClaims)
          .setSubject(userDetails.getUsername())
          .setIssuedAt(iatDate)
          .setExpiration(expDate)
          .signWith(getSignInKey(), SignatureAlgorithm.HS256)
          .compact();

    return JwtToken.builder()
            .issuedAt(iatLocalDateTime)
            .expiredAt(expLocalDateTime)
            .token(token)
            .build();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key  getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(accessSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
