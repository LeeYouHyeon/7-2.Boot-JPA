package com.example.bootJPA.security;

import com.example.bootJPA.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    // redirect 데이터를 가지고 리다이렉트 경로 저장(이동) 역할
    private RedirectStrategy redirectStrategy =
            new DefaultRedirectStrategy();
    // session 정보, 직전 URL 정보를 가지고 있는 객체
    private RequestCache requestCache =
            new HttpSessionRequestCache();

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        // 1. lastLogin 기록
        boolean isOk = userService.updateLastLogin(authentication.getName());

        HttpSession session = request.getSession();
        if (!isOk || session == null) return;

        // 이전 로그인 실패 기록 삭제
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // 이전 매핑 경로 가져오기
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        redirectStrategy.sendRedirect(request, response,
                savedRequest != null ? savedRequest.getRedirectUrl()  : "/");

    }
}
