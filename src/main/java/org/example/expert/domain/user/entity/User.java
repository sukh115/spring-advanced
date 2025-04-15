package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.util.ObjectUtils;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    private User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void validateNewPassword(String oldPassword, String newPassword, PasswordEncoder passwordEncoder) {
        if (passwordEncoder.matches(newPassword, this.password)) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }
    }

    public void validateCurrentPassword(String oldPassword, String newPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(oldPassword, this.password))
            throw new InvalidRequestException("잘못된 비밀번호 입니다");
    }

    public void validateNotSelf(User targetUser) {
        if (ObjectUtils.nullSafeEquals(this.id, targetUser.getId())) {
            throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
        }
    }

}
