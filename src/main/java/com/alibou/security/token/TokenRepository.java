package com.alibou.security.token;

import com.alibou.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  Optional<Token> findByTokenAndType(String token, TokenType type);

  Optional<Token> findByUser(User user);

  Optional<Token> findByUserAndType(User user, TokenType type);
}
