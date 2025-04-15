package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminAccessInterceptor 동작 테스트")
class AdminAccessInterceptorTest {

    private AdminAccessInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new AdminAccessInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void authUser가_있으면_로그를_남기고_true를_반환한다() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "admin@example.com", UserRole.ADMIN);
        request.setAttribute("authUser", authUser);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
    }

    @Test
    void authUser가_없어도_true를_반환한다() throws Exception {
        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
    }
}
