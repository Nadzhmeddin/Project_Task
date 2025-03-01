package ru.project.task_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.project.task_service.entity.enums.UserRole;

import java.util.List;

@Entity
@Table(name = "users_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Task> authors_task;

    @JsonIgnore
    @OneToMany(mappedBy = "executor")
    private List<Task> executors_task;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
