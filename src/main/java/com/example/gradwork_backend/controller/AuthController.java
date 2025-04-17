package com.example.gradwork_backend.controller;

import com.example.gradwork_backend.dto.LoginRequest;
import com.example.gradwork_backend.dto.LoginResponse;
import com.example.gradwork_backend.dto.RegisterRequest;
import com.example.gradwork_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String username = userService.login(request);
        LoginResponse response = new LoginResponse();
        response.setMessage("Login successful");
        response.setUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint accessed");
    }
}
