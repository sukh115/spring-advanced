package org.example.expert.domain.user.repository;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
    }
}
