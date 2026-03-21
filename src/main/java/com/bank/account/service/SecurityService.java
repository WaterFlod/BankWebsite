package com.bank.account.service;

import com.bank.account.dto.LoginRequest;
import com.bank.account.dto.RegistrationRequest;
import com.bank.account.dto.UserDTO;
import com.bank.account.model.User;
import com.bank.account.repository.UserRepository;
import com.bank.account.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public User registerUser(RegistrationRequest requset) {
        if (userRepository.exsistsByEmail(requset.email())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = User.builder()
                .email(requset.email())
                .password(passwordEncoder.encode(requset.password()))
                .firstName(requset.firstName())
                .lastName(requset.lastName())
                .build();

        user = userRepository.save(user);

        log.info("User registred: {}", user.getEmail());

        return user;
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);

        User user = (User) authentication.getPrincipal();

        return new JwtResponse(jwt, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    private UserDTO convertToDTO(User user) {

        return UserDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}