package com.example.bootJPA.controller;

import com.example.bootJPA.dto.UserDTO;
import com.example.bootJPA.handler.PagingHandler;
import com.example.bootJPA.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.print.Pageable;
import java.security.Principal;

@RequestMapping("/user/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public void join() {}

    @PostMapping("/join")
    public String join(UserDTO userDTO, Model model) {
        log.info(">>>> userDTO >> {}", userDTO);
        userDTO.encodePassword(passwordEncoder);

        if (userService.register(userDTO) != null) {
            model.addAttribute("msg", "회원가입에 성공했습니다. 로그인해주세요.");
            return "redirect:/user/login";
        } else return "redirect:/user/join";
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

    @PostMapping("/update")
    public String update(UserDTO userDTO) {
        userService.update(userDTO);
        return "redirect:/user/logout";
    }

    @GetMapping("/remove")
    public String remove(Principal principal) {
        String email = userService.remove(principal.getName());
        return "redirect:/user/logout";
    }

    @GetMapping("/list")
    public void list(Model model,
                     @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                     @RequestParam(name = "type", required = false) String type,
                     @RequestParam(name = "keyword", required = false) String keyword) {
        Page<UserDTO> list = userService.getList(pageNo, 10, type, keyword);
        model.addAttribute("ph", new PagingHandler<>(list, type, keyword));
    }
}
