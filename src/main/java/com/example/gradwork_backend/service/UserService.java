package com.example.gradwork_backend.service;

import com.example.gradwork_backend.dto.LoginRequest;
import com.example.gradwork_backend.dto.RegisterRequest;
import com.example.gradwork_backend.entity.User;
import com.example.gradwork_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // 明文存储密码
        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getPassword().equals(user.getPassword())) { // 明文比较密码
            throw new RuntimeException("Invalid password");
        }

        return user.getUsername();
    }
}
