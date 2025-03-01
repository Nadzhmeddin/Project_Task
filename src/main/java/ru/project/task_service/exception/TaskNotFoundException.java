package ru.project.task_service.exception;

public class TaskNotFoundException extends Exception{

    public TaskNotFoundException(String message) {
        super(message);
    }
}
