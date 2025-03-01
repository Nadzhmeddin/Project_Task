package ru.project.task_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.project.task_service.entity.Task;
import ru.project.task_service.entity.User;
import ru.project.task_service.exception.TaskNotFoundException;
import ru.project.task_service.repository.CommentRepository;
import ru.project.task_service.repository.TaskRepository;
import ru.project.task_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckUserService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findUserByEmailIs(email);
        if(user.isPresent()) {
            return user;
        } else throw new UsernameNotFoundException(email);
    }

    public boolean checkUser(Long id) throws TaskNotFoundException {
        Optional<Task> foundTask = taskRepository.findById(id);
        if(foundTask.isPresent()) {
            User user = getCurrentUser().get();
            return foundTask.get().getExecutor().getId().equals(user.getId());
        }
        else throw new TaskNotFoundException(String.format("Task with id: %d not found", id));
    }
}
