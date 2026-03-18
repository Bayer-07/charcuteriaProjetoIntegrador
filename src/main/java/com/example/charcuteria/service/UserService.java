package com.example.charcuteria.service;

import java.util.Optional;

import com.example.charcuteria.dto.UserRegistrationDto;
import com.example.charcuteria.model.User;
import com.example.charcuteria.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createUser(UserRegistrationDto user) {
        Optional<User> userOpt = userRepository.existsByEmail(user.getEmail());

        if (userOpt.isPresent()) {
            // erro decente quando eu tiver vontade
            // throw new BusinessException("Este email já está cadastrado.");
            return "";
        }
        // hash password

        userRepository.createUser(user);
    }
}
