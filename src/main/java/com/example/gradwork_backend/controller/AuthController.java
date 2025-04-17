package com.example.gradwork_backend.controller;

import com.example.gradwork_backend.dto.AddFriendRequest;
import com.example.gradwork_backend.dto.FriendListResponse;
import com.example.gradwork_backend.dto.LoginRequest;
import com.example.gradwork_backend.dto.LoginResponse;
import com.example.gradwork_backend.dto.RegisterRequest;
import com.example.gradwork_backend.dto.RemoveFriendRequest;
import com.example.gradwork_backend.enums.ResultEnums;
import com.example.gradwork_backend.service.UserService;
import com.example.gradwork_backend.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResultVo<String>> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_IS_EXIST.Code(), ResultEnums.ENTITY_IS_EXIST.Desc()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResultVo<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            String username = userService.login(request);
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setUsername(username);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), response));
        } catch (IllegalArgumentException e) {
            if ("User not found".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ACCOUNT_ERROR.Code(), ResultEnums.ACCOUNT_ERROR.Desc()));
            }
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ResultVo<String>> test() {
        return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Test endpoint accessed"));
    }

    @PostMapping("/friends/add")
    public ResponseEntity<ResultVo<String>> addFriend(@RequestBody AddFriendRequest request) {
        try {
            userService.addFriend(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Friend added successfully"));
        } catch (IllegalArgumentException e) {
            if ("User not found".equals(e.getMessage()) || "Friend not found".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if ("Friend already exists".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_IS_EXIST.Code(), ResultEnums.ENTITY_IS_EXIST.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @DeleteMapping("/friends/remove")
    public ResponseEntity<ResultVo<String>> removeFriend(@RequestBody RemoveFriendRequest request) {
        try {
            userService.removeFriend(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Friend removed successfully"));
        } catch (IllegalArgumentException e) {
            if ("User not found".equals(e.getMessage()) || "Friend not found".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if ("Friendship does not exist".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_NO_RECORD.Code(), ResultEnums.ERROR_NO_RECORD.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @GetMapping("/friends/list")
    public ResponseEntity<ResultVo<FriendListResponse>> getFriends(@RequestParam String username) {
        try {
            FriendListResponse response = userService.getFriends(username);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
        }
    }
}
