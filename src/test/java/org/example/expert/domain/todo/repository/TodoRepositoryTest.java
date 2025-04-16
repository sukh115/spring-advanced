package org.example.expert.domain.todo.repository;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Todo 저장 및 findByIdOrThrow 정상 동작")
    void findByIdOrThrow_success() {
        // given
        User user = userRepository.save(new User("test@naver.com", "1234", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "contents", "weather", user));

        // when
        Todo found = todoRepository.findByIdOrThrow(todo.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("findByIdOrThrow 실패 시 예외 반환")
    void findByIdOrThrow_fail() {
        assertThatThrownBy(() -> todoRepository.findByIdOrThrow(999L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Manager not found");
    }

    @Test
    @DisplayName("getByIdWithUserOrElseThrow 정상 동작")
    void getByIdWithUserOrElseThrow_success() {
        // given
        User user = userRepository.save(new User("test@naver.com", "1234", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "contents", "weather", user));

        // when
        Todo found = todoRepository.getByIdWithUserOrElseThrow(todo.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUser().getEmail()).isEqualTo("test@naver.com");
    }

    @Test
    @DisplayName("getByIdWithUserOrElseThrow 실패 시 예외 반환")
    void getByIdWithUserOrElseThrow_fail() {
        assertThatThrownBy(() -> todoRepository.getByIdWithUserOrElseThrow(999L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Todo not found");
    }

    @Test
    @DisplayName("findAllByOrderByModifiedAtDesc 정상 작동")
    void findAllByOrderByModifiedAtDesc_success() {
        // given
        User user = userRepository.save(new User("test@naver.com", "1234", UserRole.USER));
        todoRepository.save(new Todo("title1", "contents1", "weather1", user));
        todoRepository.save(new Todo("title2", "contents2", "weather2", user));

        // when
        Page<Todo> page = todoRepository.findAllByOrderByModifiedAtDesc(PageRequest.of(0, 10));

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("countById는 Todo 존재 시 1 반환")
    void countById_success() {
        // given
        User user = userRepository.save(new User("count@naver.com", "pass", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("count-title", "count-contents", "cloudy", user));

        // when
        int count = todoRepository.countById(todo.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("countById는 Todo 존재하지 않으면 0 반환")
    void countById_fail() {
        int count = todoRepository.countById(999L);
        assertThat(count).isZero();
    }
}
