package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        User loginUser = User.fromAuthUser(authUser);

        Todo todo = todoRepository.findByIdOrThrow(todoId);
        todo.validateOwner(loginUser);

        User managerUser = userRepository.findByIdOrElseThrow(managerSaveRequest.getManagerUserId());
        loginUser.validateNotSelf(managerUser);

        Manager manager = managerRepository.save(new Manager(managerUser, todo));
        manager.validateDuplicateManager(loginUser);

        return new ManagerSaveResponse(manager.getId(), new UserResponse(managerUser.getId(), managerUser.getEmail()));
    }


    @Transactional(readOnly = true)
    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findByIdOrThrow(todoId);

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    @Transactional
    public void deleteManager(long userId, long todoId, long managerId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        Todo todo = todoRepository.findByIdOrThrow(todoId);

        todo.validateOwner(user);

        Manager manager = managerRepository.getByIdOrThrow(managerId);

        manager.validateBelongsTo(todo);

        managerRepository.delete(manager);
    }
}
