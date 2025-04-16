package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저 조회 성공")
    void getUserSuccess() {
        // given
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("test@example.com");

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(mockUser);

        // when
        UserResponse response = userService.getUser(1L);

        // then
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
    }
    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() {
        // given
        User user = mock(User.class);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPw", "newPw");

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
        when(passwordEncoder.encode("newPw")).thenReturn("encodedNewPw");

        // when
        userService.changePassword(1L, request);

        // then
        verify(user).validateNewPassword("oldPw", "newPw", passwordEncoder);
        verify(user).validateCurrentPassword("oldPw", passwordEncoder);
        verify(user).changePassword("encodedNewPw");
    }

    @Test
    @DisplayName("비밀번호 검증 실패 시 예외 발생")
    void changePasswordFailsWhenInvalidPassword() {
        // given
        User user = mock(User.class);
        UserChangePasswordRequest request = new UserChangePasswordRequest("wrongOldPw", "newPw");

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(user);
        doThrow(new InvalidRequestException("비밀번호 틀림"))
                .when(user).validateCurrentPassword("wrongOldPw", passwordEncoder);

        // when & then
        assertThrows(InvalidRequestException.class, () -> userService.changePassword(1L, request));
    }
}