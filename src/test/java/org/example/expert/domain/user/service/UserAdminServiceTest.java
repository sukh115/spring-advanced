package org.example.expert.domain.user.service;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
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
class UserAdminServiceTest {

    @InjectMocks
    private UserAdminService userAdminService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 역할 변경 성공")
    void changeUserRole() {
        // given
        long userId = 1L;
        UserRoleChangeRequest request = new UserRoleChangeRequest("USER");

        User user = mock(User.class);

        when(userRepository.findByIdOrElseThrow(userId)).thenReturn(user);


        // when
        userAdminService.changeUserRole(userId, request);

        // then
        verify(user).updateRole(UserRole.USER);
    }
}