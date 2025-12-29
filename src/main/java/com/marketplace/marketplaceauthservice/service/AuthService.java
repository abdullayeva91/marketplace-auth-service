package com.marketplace.marketplaceauthservice.service;

import com.marketplace.marketplaceauthservice.config.JwtTokenProvider;
import com.marketplace.marketplaceauthservice.enums.Role;
import com.marketplace.marketplaceauthservice.model.MyUserDetails;
import com.marketplace.marketplaceauthservice.model.User;
import com.marketplace.marketplaceauthservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User registerUser(String username, String password, String email, Role role) {
        if (userRepository.existsUserByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setRole(role != null ? role : Role.USER);

        return userRepository.save(newUser);
    }

    public String login(String username, String plainPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Username not found");
        }

        if (!passwordEncoder.matches(plainPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        MyUserDetails userDetails = new MyUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getRole().name() // "ADMIN" or "USER"
        );

        return jwtTokenProvider.generateToken(userDetails);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }
}