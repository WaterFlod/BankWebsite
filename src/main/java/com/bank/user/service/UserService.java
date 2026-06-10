package com.bank.user.service;

import com.bank.user.exception.UserNotFoundException;
import com.bank.user.model.Role;
import com.bank.user.model.User;
import com.bank.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsUserByPhoneNumber(String phoneNumber) { return userRepository.existsByPhoneNumber(phoneNumber); }

    public User findUserByIdentifier(String identifier) {
        Optional<User> user = userRepository.findByEmail(identifier);
        return user.get();
    }

    public User registerUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        User user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        log.info("Новый пользователь создан {}", email);

        return user;
    }

    public void setPassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("Пароль установлен");
    }
}
