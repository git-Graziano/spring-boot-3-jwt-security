package com.alibou.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  @Query("select u from User u " +
          "inner join PasswordRecovery pr on u.id = pr.id " +
          "where pr.token = :token")
  Optional<User> findByPasswordRecoveriesToken(String token);

}
