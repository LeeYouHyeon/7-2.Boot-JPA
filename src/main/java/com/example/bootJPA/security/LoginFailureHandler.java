package com.example.bootJPA.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    String errorMessage; // 에러 메시지 저장 변수
    String loginEmail = request.getParameter("email");

    if (exception instanceof BadCredentialsException) {
      // email + pwd 오류
      errorMessage = "이메일 또는 비밀번호가 맞지 않습니다.";
    } else if (exception instanceof InternalAuthenticationServiceException) {
      errorMessage = "관리자에게 문의해주세요.";
    } else errorMessage = "알 수 없는 오류";
    log.info(errorMessage);

    //인코딩 처리
    errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

    setDefaultFailureUrl("/user/loginFailed?errmsg=" + errorMessage);
    super.onAuthenticationFailure(request, response, exception);
//        request.getRequestDispatcher("/user/loginFailed")
//                .forward(request, response);
  }
}
