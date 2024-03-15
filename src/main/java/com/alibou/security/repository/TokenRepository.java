package com.alibou.security.repository;

import com.alibou.security.model.Token;
import com.alibou.security.model.TokenType;
import com.alibou.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  Optional<Token> findByTokenAndType(String token, TokenType type);

  Optional<Token> findByUser(User user);

  Optional<Token> findByUserAndType(User user, TokenType type);
}
