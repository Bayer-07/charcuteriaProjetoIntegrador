package com.example.charcuteria.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.UserRegistrationDto;
import com.example.charcuteria.dto.UserResponseDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ErrorCode;
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
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
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

    public Optional<UserResponseDto> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.of(new UserResponseDto(user.getId(), user.getName()));
        } else {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
