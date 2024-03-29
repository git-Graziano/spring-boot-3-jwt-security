package com.alibou.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spn_token")
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "TOKEN")
  private String token;

  @Column(name = "TYPE")
  @Enumerated(EnumType.STRING)
  private TokenType type = TokenType.ACCESS;

  @Column(name = "ISSUED_AT")
  private LocalDateTime issuedAt;

  @Column(name = "EXPIRED_AT")
  private LocalDateTime expiredAt;

  @Column(name = "ALLOWED")
  private Boolean allowed;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
}
