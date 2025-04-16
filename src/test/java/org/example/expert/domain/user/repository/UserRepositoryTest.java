package org.example.expert.domain.user.repository;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회에 성공한다.")
    void existsByEmailOrElseThrow_success() {
        // given
        User user = new User("test@example.com", "password", UserRole.USER);
        userRepository.save(user);

        // when
        User result = userRepository.existsByEmailOrElseThrow("test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 실패 시 예외를 던진다.")
    void existsByEmailOrElseThrow_fail() {
        // expect
        assertThatThrownBy(() -> userRepository.existsByEmailOrElseThrow("nonexist@example.com"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Email not found");
    }

    @Test
    @DisplayName("ID로 사용자 조회에 성공한다.")
    void findByIdOrElseThrow_success() {
        // given
        User user = new User("idtest@example.com", "password", UserRole.USER);
        User saved = userRepository.save(user);

        // when
        User result = userRepository.findByIdOrElseThrow(saved.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("idtest@example.com");
    }

    @Test
    @DisplayName("ID로 사용자 조회 실패 시 예외를 던진다.")
    void findByIdOrElseThrow_fail() {
        // expect
        assertThatThrownBy(() -> userRepository.findByIdOrElseThrow(999L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User not found");
    }
}
