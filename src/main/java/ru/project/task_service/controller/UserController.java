package ru.project.task_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.project.task_service.entity.User;
import ru.project.task_service.security.CustomUserDetailsService;
import ru.project.task_service.service.UserService;
import ru.project.task_service.webtoken.JwtService;
import ru.project.task_service.webtoken.LoginForm;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "API for working with Users")
public class UserController {

    private final UserService service;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Get all users",
            description = "Find list all created users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Users not found"
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll() {
        List<User> allUsers = service.findAll();
        if(!allUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(allUsers);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Find user by ID",
            description = "Find user by his id",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User find"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "User not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findById(@PathVariable Long id) {
        Optional<User> foundUser = service.findById(id);
        if(foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(foundUser);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Create user",
            description = "Register user, where password BCrypt encoded",
            responses = @ApiResponse(
                    responseCode = "201",
                    description = "User is created"
            )
    )
    @PostMapping("/register")
    public ResponseEntity<User> save(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    @Operation(
            summary = "Authenticate the user",
            description = "Authenticate the user and assign him a JWT token"
    )
    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.email(),
                loginForm.password()
        ));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(userDetailsService.loadUserByUsername(loginForm.email()));
        } else throw new UsernameNotFoundException(loginForm.email());
    }

    @Operation(
            summary = "Delete User",
            description = "Delete User by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Update User by ID",
            description = "Found User by Id and update his Details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task is updated"
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> updateById(@PathVariable Long id, @RequestBody User user) {
        Optional<User> foundUser = service.findById(id);
        if(foundUser.isPresent()) {
            User updatedUser = foundUser.get();
            updatedUser.setEmail(user.getEmail());
            updatedUser.setRole(user.getRole());
            updatedUser.setPassword(user.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(service.save(updatedUser));
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
