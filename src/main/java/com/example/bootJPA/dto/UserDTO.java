package com.example.bootJPA.dto;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Builder
public class UserDTO {
  private String email;
  private String pwd;
  private String nickName;
  private LocalDateTime lastLogin, regDate, modDate;
  private List<AuthUserDTO> authList;
  private String profile;

  public void encodePassword(PasswordEncoder passwordEncoder) {
    setPwd(passwordEncoder.encode(getPwd()));
  }

  public String[] getAuthStrings() {
    String[] answer = new String[authList.size()];
    int idx = 0;
    for (AuthUserDTO auth : authList) {
      answer[idx] = auth.getAuth();
      idx++;
    }
    return answer;
  }

  public String getProfileURL() {
    return profile == null ? "/image/noProfile.png" : "/upload/_profile/" + profile;
  }
}
