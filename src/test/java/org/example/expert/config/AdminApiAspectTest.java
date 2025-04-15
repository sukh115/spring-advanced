package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminApiAspectTest {

    private AdminApiAspect aspect;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aspect = new AdminApiAspect(objectMapper);
    }

    @Test
    void logAdminApi_정상_호출_및_로그_출력_성공() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-body"});

        when(joinPoint.proceed()).thenReturn("response-body");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/admin/test");

        AuthUser authUser = new AuthUser(1L, "admin@example.com", UserRole.ADMIN);
        when(mockRequest.getAttribute("authUser")).thenReturn(authUser);

        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(mockRequest);

        try (MockedStatic<RequestContextHolder> mockedContext = mockStatic(RequestContextHolder.class)) {
            mockedContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(attributes);

            // when
            Object result = aspect.logAdminApi(joinPoint);

            // then
            assertEquals("response-body", result); // 결과 반환 확인
            // 로그 출력은 assert로 체크하지 않음
        }
    }
}