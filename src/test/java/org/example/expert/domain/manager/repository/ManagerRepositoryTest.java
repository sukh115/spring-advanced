package org.example.expert.domain.manager.repository;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ManagerRepositoryTest {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("getByIdOrThrow 성공 케이스")
    void getByIdOrThrow_success() {
        // given
        User user = userRepository.save(new User("test@email.com", "pass", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "content", "sunny", user));
        Manager saved = managerRepository.save(new Manager(user, todo));

        // when
        Manager found = managerRepository.getByIdOrThrow(saved.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUser().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("getByIdOrThrow 실패 시 예외 발생")
    void getByIdOrThrow_fail() {
        assertThatThrownBy(() -> managerRepository.getByIdOrThrow(999L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Manager not found");
    }

    @Test
    @DisplayName("findByTodoIdWithUser로 Todo에 연결된 매니저 조회 성공")
    void findByTodoIdWithUser_success() {
        // given
        User user = userRepository.save(new User("manager@email.com", "pw", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("todo title", "todo contents", "rainy", user));
        Manager manager = managerRepository.save(new Manager(user, todo));

        // when
        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        // then
        assertThat(managerList).isNotEmpty();
        assertThat(managerList.get(0).getUser().getEmail()).isEqualTo("manager@email.com");
    }

    @Test
    @DisplayName("findByTodoIdWithUser 결과 없음")
    void findByTodoIdWithUser_empty() {
        // when
        List<Manager> result = managerRepository.findByTodoIdWithUser(999L);

        // then
        assertThat(result).isEmpty();
    }
}
