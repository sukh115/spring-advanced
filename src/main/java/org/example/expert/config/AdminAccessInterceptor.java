package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuthUser authUser = (AuthUser) request.getAttribute("authUser");

        if (authUser != null) {
            log.info("[ADMIN ACCESS] userId={}, method={}, uri={}, time={}",
                    authUser.getId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
        }
        return true;
    }
}
