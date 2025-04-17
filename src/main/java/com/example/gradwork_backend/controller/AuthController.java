package com.example.gradwork_backend.controller;

import com.example.gradwork_backend.dto.AddFriendRequest;
import com.example.gradwork_backend.dto.LoginRequest;
import com.example.gradwork_backend.dto.LoginResponse;
import com.example.gradwork_backend.dto.RegisterRequest;
import com.example.gradwork_backend.dto.RemoveFriendRequest;
import com.example.gradwork_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String username = userService.login(request);
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setUsername(username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint accessed");
    }

    @PostMapping("/friends/add")
    public ResponseEntity<String> addFriend(@RequestBody AddFriendRequest request) {
        try {
            userService.addFriend(request);
            return ResponseEntity.ok("Friend added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/friends/remove")
    public ResponseEntity<String> removeFriend(@RequestBody RemoveFriendRequest request) {
        try {
            userService.removeFriend(request);
            return ResponseEntity.ok("Friend removed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
