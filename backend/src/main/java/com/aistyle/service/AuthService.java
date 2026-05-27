package com.aistyle.service;

import com.aistyle.dto.SignupRequest;
import com.aistyle.dto.AuthResponse;
import com.aistyle.entity.User;
import com.aistyle.exception.BadRequestException;
import com.aistyle.repository.UserRepository;
import com.aistyle.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .build();

        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail(), savedUser.getId());

        return AuthResponse.builder()
            .token(token)
            .userId(savedUser.getId())
            .email(savedUser.getEmail())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .role(savedUser.getRole().name())
            .message("User registered successfully")
            .build();
    }
}
