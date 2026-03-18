package com.example.charcuteria.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.UserRegistrationDto;
import com.example.charcuteria.model.User;
import com.example.charcuteria.repository.UserRepository;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserRegistrationDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            // erro decente quando eu tiver vontade
            // throw new BusinessException("Email already registered");
            throw new RuntimeException();
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());

        User newUser = new User(
            user.getName(),
            user.getEmail(),
            hashedPassword,
            user.getRole()
        );

        userRepository.createUser(newUser);
    }
}
