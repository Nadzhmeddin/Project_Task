package ru.project.task_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.project.task_service.entity.User;
import ru.project.task_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmailIs(username);
        if(user.isPresent()) {
            User newUser = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(newUser.getEmail())
                    .password(newUser.getPassword())
                    .roles(getRoles(newUser))
                    .build();
        } else throw new UsernameNotFoundException(username);
    }

    private String[] getRoles(User user) {
        if(user.getRole().getRole().isEmpty()) {
            return new String[]{"USER"};
        } else return user.getRole().getRole().split(",");
    }
}
