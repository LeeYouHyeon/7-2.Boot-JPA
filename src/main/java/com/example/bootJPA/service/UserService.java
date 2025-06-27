package com.example.bootJPA.service;

import com.example.bootJPA.dto.AuthUserDTO;
import com.example.bootJPA.dto.UserDTO;
import com.example.bootJPA.entity.AuthUser;
import com.example.bootJPA.entity.User;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    default AuthUserDTO convertEntityToDto(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .auth(authUser.getAuth())
                .build();
    }
    default List<AuthUserDTO> convertEntityToDto(List<AuthUser> authUserList) {
        return authUserList.stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    default AuthUser convertDtoToEntity(AuthUserDTO authUserDTO) {
        return AuthUser.builder()
                .id(authUserDTO.getId())
                .email(authUserDTO.getEmail())
                .auth(authUserDTO.getAuth())
                .build();
    }

    default UserDTO convertEntityToDto(User user, List<AuthUser> authUserList) {
        return UserDTO.builder()
                .email(user.getEmail())
                .pwd(user.getPwd())
                .nickName(user.getNickName())
                .lastLogin(user.getLastLogin())
                .regDate(user.getRegDate())
                .modDate(user.getModDate())
                .authList(convertEntityToDto(authUserList))
                .build();
    }

    default User convertDtoToEntity(UserDTO userDTO) {
        return User.builder()
                .email(userDTO.getEmail())
                .pwd(userDTO.getPwd())
                .nickName(userDTO.getNickName())
                .lastLogin(userDTO.getLastLogin())
                .build();
    }

    default AuthUser defaultAuth(String email) {
        return AuthUser.builder()
                .email(email)
                .auth("ROLE_USER")
                .build();
    }

    String register(UserDTO userDTO);

    UserDTO selectEmail(String username);

    boolean updateLastLogin(String email);

    void update(UserDTO userDTO);

    String remove(String name);

    Page<UserDTO> getList(int pageNo, int i, String type, String keyword);
}
