package ru.project.task_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.project.task_service.entity.Task;
import ru.project.task_service.entity.enums.TaskPriority;
import ru.project.task_service.entity.enums.TaskStatus;
import ru.project.task_service.exception.TaskExecutorException;
import ru.project.task_service.exception.TaskNotFoundException;
import ru.project.task_service.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final CheckUserService checkUserService;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public void updateTaskStatus(Long id, TaskStatus status) throws TaskExecutorException, TaskNotFoundException {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()) {
            if(checkUserService.checkUser(id)) {
                task.get().setStatus(status);
                taskRepository.save(task.get());
            } else throw new TaskExecutorException(String.format(
                    "Task with id: %d does not belong to user with id: %s", id, task.get().getExecutor().getId()));
        } else throw new TaskNotFoundException(String.format("Task with id: %d not found", id));
    }

    public Task updateTaskPriority(Long id, TaskPriority priority) {
        Optional<Task> foundTask = taskRepository.findById(id);
        Task task = foundTask.get();
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    public List<Task> findAllTaskByExecutor(Long id) {
        return taskRepository.findAllByExecutorId(id);
    }
    public List<Task> findAllTaskByAuthor(Long id) {
        return taskRepository.findAllByAuthorId(id);
    }
}
