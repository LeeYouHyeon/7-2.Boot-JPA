package com.example.bootJPA.controller;

import com.example.bootJPA.dto.UserDTO;
import com.example.bootJPA.handler.FileHandler;
import com.example.bootJPA.handler.PagingHandler;
import com.example.bootJPA.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RequestMapping("/user/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/join")
  public void join() {
  }

  @ResponseBody
  @PostMapping("/join")
  public String join(@RequestPart("userDTO") UserDTO userDTO,
                     @RequestPart(name = "profile", required = false) MultipartFile profile,
                     Model model) {
    log.info(">>>> /user/join in");
    log.info(">>>> userDTO >> {}", userDTO);
    log.info(">>>> profile >> {}", profile);
    userDTO.encodePassword(passwordEncoder);

    if (profile != null && !profile.isEmpty()) try {
      String file = new FileHandler().uploadProfile(profile, userDTO.getEmail());
      userDTO.setProfile(file);
      log.info(">>>> profile image registered");
    } catch (Exception e) {
      return e.getMessage();
    }

    return userService.register(userDTO) == null ? "0" : "1";
  }

  @GetMapping("/login")
  public void login() {
  }

  @GetMapping("/loginFailed")
  public String login(String errorMessage, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errmsg", errorMessage);
    return "redirect:/user/login";
  }

  @GetMapping("/info")
  public void info(Model model, Principal principal) {
    model.addAttribute("userDTO", userService.selectEmail(principal.getName()));
  }

  @ResponseBody
  @PostMapping("/update")
  public String update(
      @RequestPart(name = "userDTO") UserDTO userDTO,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {
    if (profile != null && !profile.isEmpty()) try {
      userDTO.setProfile(
          new FileHandler().uploadProfile(
              profile, userDTO.getEmail()));
    } catch (Exception e) {
      return "프로필 이미지 업로드에 실패했습니다.";
    }
    return String.valueOf(userService.update(userDTO));
  }

  @GetMapping("/list")
  public void list(
      Model model,
      @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "keyword", required = false) String keyword
  ) {
    Page<UserDTO> list = userService.getList(pageNo, 10, type, keyword);
    model.addAttribute("ph", new PagingHandler<>(list, type, keyword));
  }

  private void logout(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    Authentication auth = SecurityContextHolder
        .getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
  }

  @ResponseBody
  @GetMapping("/check")
  public String emailCheck(@RequestParam(name = "email") String email) {
    return userService.available(email);
  }

  @GetMapping("/modify")
  public void modify() {
  }

  @ResponseBody
  @PostMapping("/match")
  public String match(@RequestBody UserDTO userDTO) {
    return userService.match(userDTO);
  }

  @GetMapping("/changePwd")
  public void changePwd() {
  }

  @GetMapping("/remove")
  public String remove(
      Principal principal,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    String email = userService.remove(principal.getName());
    logout(request, response);
    return "redirect:/";
  }

  @PostMapping("/changePwd")
  @ResponseBody
  public String changePwd(@RequestBody UserDTO userDTO) {
    userDTO.encodePassword(passwordEncoder);
    return userService.changePwd(userDTO);
  }
}
