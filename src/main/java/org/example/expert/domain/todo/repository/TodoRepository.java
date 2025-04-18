package org.example.expert.domain.todo.repository;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);

    default Todo getByIdWithUserOrElseThrow(Long todoId) {
        return findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
    }

    default Todo findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new InvalidRequestException("Manager not found"));
    }
}
