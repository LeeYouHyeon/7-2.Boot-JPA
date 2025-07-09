package com.example.bootJPA.security;

import com.example.bootJPA.dto.UserDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class AuthMember extends User {

  @Getter
  private UserDTO userDTO;

  public AuthMember(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public AuthMember(UserDTO userDTO) {
    this(userDTO.getEmail(),
        userDTO.getPwd(),
        userDTO.getAuthList()
            .stream()
            .map(authUser -> new SimpleGrantedAuthority(authUser.getAuth()))
            .collect(Collectors.toList()));
    this.userDTO = userDTO;
  }

}
