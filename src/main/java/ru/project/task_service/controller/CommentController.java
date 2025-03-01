package ru.project.task_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.project.task_service.entity.Comment;
import ru.project.task_service.exception.TaskExecutorException;
import ru.project.task_service.exception.TaskNotFoundException;
import ru.project.task_service.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment controller", description = "API for working with Comments")
public class CommentController {

    private final CommentService service;
    @Operation(
            summary = "Add comment to task",
            description = "Search for a task by ID and add a comment to it",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment added"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @checkUserService.checkUser(#id)")
    public ResponseEntity<Comment> addComment(@PathVariable Long id, @RequestBody Comment comment) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addComment(id, comment));
        } catch (TaskNotFoundException e) {
            throw new RuntimeException(e);
        } catch (TaskExecutorException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(
            summary = "Get all comments for a task",
            description = "Find all comments to task by task Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All comments to task have been found"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No comments found for this task"
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Comment>> findCommentByTaskId(@PathVariable Long id) {
        List<Comment> comments = service.findCommentByTaskId(id);
        if(!comments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(comments);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Delete comment by id",
            description = "Find comment by id and deleted",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Comment deleted"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
