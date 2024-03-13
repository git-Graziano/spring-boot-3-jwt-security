package com.alibou.security.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spn_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "FIRST_NAME")
  private String firstname;

  @Column(name = "LAST_NAME")
  private String lastname;

  @Column(name = "EMAIL", unique = true)
  private String email;

  @Column(name = "PASSWORD")
  private String password;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
          name = "spn_user_authority",
          joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") },
          inverseJoinColumns = { @JoinColumn(name = "authority_id", referencedColumnName = "id") }
  )
  private Collection<Authority> authorities;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

//  @OneToMany(mappedBy = "spn_user")
//  private List<PasswordRecovery> passwordRecovery;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
