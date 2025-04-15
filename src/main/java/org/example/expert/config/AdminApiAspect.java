package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminApiAspect {

    private final ObjectMapper objectMapper;

    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 요청 객체
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        AuthUser authUser = (AuthUser) request.getAttribute("authUser");

        String requestBody = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return "[Serialization Failed]";
                    }
                }).collect(Collectors.joining(","));

        // 요청 로그
        log.info("[ADMIN API REQUEST] userId={}, time={}, uri={}, body={}",
                authUser !=null ? authUser.getId() : "UNKNOWN",
                LocalDateTime.now(),
                request.getRequestURI(),
                requestBody
                );

        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            responseBody = "[Serialization Failed]";
        }

        // 응답 로그
        log.info("[ADMIN API RESPONSE] userId={}, uri={}, body={}",
                authUser != null ? authUser.getId() : "UNKNOWN",
                request.getRequestURI(),
                responseBody
        );
        return result;
    }
}
