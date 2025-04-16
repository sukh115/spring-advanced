package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;


    @Test
    void signup_정상_동작() {
        // given
        SignupRequest request = new SignupRequest("user@example.com", "password", "USER");
        String encodedPassword = "encodedPassword";

        User savedUser = new User("user@example.com", encodedPassword, UserRole.USER);
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        when(userRepository.existsByEmailOrElseThrow("user@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.createToken(1L, "user@example.com", UserRole.USER)).thenReturn("Bearer token");

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertNotNull(response);
        assertEquals("Bearer token", response.getBearerToken());
    }


    @Test
    void signinSuccess() {
        // given
        SigninRequest request = new SigninRequest("user@example.com", "password");

        User mockUser = spy(new User("user@example.com", "encodedPassword", UserRole.USER));
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        given(userRepository.existsByEmailOrElseThrow(request.getEmail())).willReturn(mockUser);
        doNothing().when(mockUser).validateCurrentPassword(eq("password"), any(PasswordEncoder.class));
        given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class)))
                .willReturn("Bearer test-token");

        // when
        SigninResponse response = authService.signin(request);

        // then
        assertNotNull(response);
        assertEquals("Bearer test-token", response.getBearerToken());
    }

}