package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
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
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("Todo ID로 연결된 댓글 + 유저 정보를 조회할 수 있다")
    void findByTodoIdWithUser_success() {
        // given
        User user = userRepository.save(new User("test@email.com", "pass", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "contents", "clear", user));
        Comment comment = new Comment("댓글 내용", user, todo);
        commentRepository.save(comment);

        // when
        List<Comment> comments = commentRepository.findByTodoIdWithUser(todo.getId());

        // then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContents()).isEqualTo("댓글 내용");
        assertThat(comments.get(0).getUser().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("댓글이 없는 Todo ID를 조회하면 빈 리스트를 반환한다")
    void findByTodoIdWithUser_empty() {
        // when
        List<Comment> comments = commentRepository.findByTodoIdWithUser(999L);

        // then
        assertThat(comments).isEmpty();
    }
}
