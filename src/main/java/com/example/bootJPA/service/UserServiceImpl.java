package com.example.bootJPA.service;

import com.example.bootJPA.dto.AuthUserDTO;
import com.example.bootJPA.dto.UserDTO;
import com.example.bootJPA.entity.AuthUser;
import com.example.bootJPA.entity.User;
import com.example.bootJPA.repository.AuthRepository;
import com.example.bootJPA.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Transactional
    @Override
    public String register(UserDTO userDTO) {
        String email = userRepository.save(this.convertDtoToEntity(userDTO)).getEmail();
        if (email == null) return null;

        authRepository.save(this.defaultAuth(email));
        return email;
    }

    @Override
    public UserDTO selectEmail(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return convertEntityToDto(user, authRepository.findByEmail(username));
    }

    @Transactional
    @Override
    public boolean updateLastLogin(String email) {
        try {
            User user = userRepository.findById(email)
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
            user.setLastLogin(LocalDateTime.now());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    @Override
    public void update(UserDTO userDTO) {
        Optional<User> optional = userRepository.findById(userDTO.getEmail());
        if (optional.isEmpty()) return;
        optional.get().setNickName(userDTO.getNickName());
    }

    @Transactional
    @Override
    public String remove(String email) {
        Optional<User> optional = userRepository.findById(email);
        if (optional.isEmpty()) return "0";

        userRepository.deleteById(email);
        authRepository.deleteByEmail(email);
        return "1";
    }

    @Override
    public Page<UserDTO> getList(int pageNo, int i, String type, String keyword) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by("regDate"));
        return userRepository.searchUser(type, keyword, pageable)
                .map(user -> convertEntityToDto(user, authRepository.findByEmail(user.getEmail())));
    }
}
