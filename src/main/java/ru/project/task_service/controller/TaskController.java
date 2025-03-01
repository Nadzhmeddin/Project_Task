package ru.project.task_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.project.task_service.entity.Task;
import ru.project.task_service.entity.enums.TaskPriority;
import ru.project.task_service.entity.enums.TaskStatus;
import ru.project.task_service.exception.TaskExecutorException;
import ru.project.task_service.exception.TaskNotFoundException;
import ru.project.task_service.service.TaskService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "API for working with Tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks",
            description = "Retrieve a list of all tasks",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List if tasks"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No tasks have been created"
                    )
            }
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> findAll() {
        List<Task> allTasks = taskService.findAll();
        if(!allTasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(allTasks);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Find task by ID",
            description = "Find task by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task found"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task not found"
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Optional<Task>> findById(@PathVariable Long id) {
        Optional<Task> foundTask = taskService.findById(id);
        if(foundTask.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(foundTask);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Create a new task",
            description = "Create a new task with the provided details",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task created"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> save(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.save(task));
    }

    @Operation(
            summary = "Updated a task status",
            description = "Found task by id and updated his status",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task status is updated"
                    )
            }
    )
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateTaskStatusByTaskId(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            taskService.updateTaskStatus(id, status);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (TaskExecutorException e) {
            throw new RuntimeException(e);
        } catch (TaskNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "Delete task by Id",
            description = "Find task by his ID and delete",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task deleted"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Find all task by executor",
            description = "Search all tasks by the specified executor",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "All task found by executor"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task by this executor not found"
                    )
            }
    )
    @GetMapping("/executor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> findAllTaskByExecutor(@PathVariable Long id) {
        List<Task> executorTasks = taskService.findAllTaskByExecutor(id);
        if(!executorTasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(executorTasks);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(
            summary = "Find all task by author",
            description = "Search all tasks by the specified author",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "All task found by author"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task by this author not found"
                    )
            }
    )
    @GetMapping("/author/{id}")
    public ResponseEntity<List<Task>> findAllTaskByAuthor(@PathVariable Long id) {
        List<Task> authorTasks = taskService.findAllTaskByAuthor(id);
        if(!authorTasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(authorTasks);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(
            summary = "Update task priority",
            description = "Find task by ID and update his priority",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Priority is updated"
                    )
            }
    )
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> updateTaskPriority(@PathVariable Long id, @RequestParam TaskPriority priority) {
        Task task = taskService.updateTaskPriority(id, priority);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
}
