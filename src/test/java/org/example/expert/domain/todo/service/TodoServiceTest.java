package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    void 일정_저장에_성공한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("title", "contents");

        User user = User.fromAuthUser(authUser);
        ReflectionTestUtils.setField(user, "id", 1L);

        String weather = "Sunny";
        Todo savedTodo = new Todo(request.getTitle(), request.getContents(), weather, user);
        ReflectionTestUtils.setField(savedTodo, "id", 10L);

        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, request);

        // then
        assertNotNull(response);
        assertEquals("title", response.getTitle());
        assertEquals("contents", response.getContents());
        assertEquals("Sunny", response.getWeather());
        assertEquals("test@example.com", response.getUser().getEmail());
        verify(weatherClient).getTodayWeather();
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void 일정_리스트_조회에_성공한다() {
        // given
        int page = 1;
        int size = 10;

        User user = new User("test@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 100L);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        Page<Todo> todoPage = new PageImpl<>(List.of(todo));

        given(todoRepository.findAllByOrderByModifiedAtDesc(PageRequest.of(0, 10))).willReturn(todoPage);

        // when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        // then
        assertEquals(1, result.getTotalElements());
        TodoResponse response = result.getContent().get(0);
        assertEquals(100L, response.getId());
        assertEquals("title", response.getTitle());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void 단일_일정_조회에_성공한다() {
        // given
        long todoId = 1L;

        User user = new User("test@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 10L);

        Todo todo = new Todo("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now().minusDays(2));
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        given(todoRepository.getByIdWithUserOrElseThrow(todoId)).willReturn(todo);

        // when
        TodoResponse result = todoService.getTodo(todoId);

        // then
        assertNotNull(result);
        assertEquals(todoId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("Sunny", result.getWeather());
        assertEquals("test@example.com", result.getUser().getEmail());
    }
}
