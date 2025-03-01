package ru.project.task_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.task_service.entity.Comment;
import ru.project.task_service.entity.Task;
import ru.project.task_service.entity.User;
import ru.project.task_service.exception.TaskExecutorException;
import ru.project.task_service.exception.TaskNotFoundException;
import ru.project.task_service.repository.CommentRepository;
import ru.project.task_service.repository.TaskRepository;
import ru.project.task_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final TaskRepository taskRepository;

    private final CheckUserService checkService;

    private final UserRepository userRepository;

    public Comment addComment(Long id, Comment comment) throws TaskNotFoundException, TaskExecutorException {
        Optional<Task> foundTask = taskRepository.findById(id);
        if(foundTask.isPresent()) {
            if(checkService.checkUser(id)) {
                Optional<User> foundUser = userRepository.findById(checkService.getCurrentUser().get().getId());
                comment.setTask(foundTask.get());
                comment.setAuthor(foundUser.get());
                return commentRepository.save(comment);
            } else throw new TaskExecutorException(String.format("Task with id: %d does not belong to user with id: %s", id,foundTask.get().getExecutor().getId()));
        } else throw new TaskNotFoundException(String.format("Task with id: %d not found", id));
    }

    public List<Comment> findCommentByTaskId(Long id) {
        return commentRepository.findByTaskId(id);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
