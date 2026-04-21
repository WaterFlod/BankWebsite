package com.bank.account.service;

import com.bank.account.dto.RegistrationRequest;
import com.bank.account.exception.UserNotFoundException;
import com.bank.account.model.Role;
import com.bank.account.model.User;
import com.bank.account.repository.UserRepository;
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

    public boolean existsUserByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User findUserByIdentifier(String identifier) {
        Optional<User> user = userRepository.findByEmail(identifier);
        return user.get();
    }

    public User registerUser(RegistrationRequest request) {
        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password("123")
                .phoneNumber(request.phoneNumber())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        log.info("Новый пользователь создан " + request.email());
        return user;
    }

    public boolean setPassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("Пароль установлен");
        return true;
    }
}
