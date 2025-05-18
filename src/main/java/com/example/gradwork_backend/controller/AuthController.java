package com.example.gradwork_backend.controller;

import com.example.gradwork_backend.dto.*;
import com.example.gradwork_backend.enums.ResultEnums;
import com.example.gradwork_backend.service.UserService;
import com.example.gradwork_backend.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                return ResponseEntity.status(200).body(ResultVo.getFail(ResultEnums.ENTITY_IS_EXIST.Code(), ResultEnums.ENTITY_IS_EXIST.Desc()));
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

    @PostMapping("/message/send")
    public ResponseEntity<ResultVo<String>> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            userService.sendMessage(request);// 调用userService的sendMessage方法发送消息
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Message sent successfully"));
        } catch (IllegalArgumentException e) {
            if ("Sender not found".equals(e.getMessage()) || "Receiver not found".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if ("Receiver is not a friend".equals(e.getMessage())) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_NO_RECORD.Code(), "Receiver is not a friend"));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @GetMapping("/message/list")
    public ResponseEntity<ResultVo<List<MessageResponse>>> getMessages(
            @RequestParam String username, @RequestParam String friendUsername) {
        try {
            List<MessageResponse> messages = userService.getMessages(username, friendUsername);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), messages));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
        }
    }

    @PostMapping("/ai/ask")
    public ResponseEntity<ResultVo<String>> askAi(@RequestBody AiAskRequest request,
                                                  @RequestParam String username) {
        try {
            String answer = userService.askAi(request.getQuestion(), username);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), answer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
        }
    }

    @GetMapping("/ai/history")
    public ResponseEntity<ResultVo<List<AiConversationResponse>>> getAiHistory(@RequestParam String username) {
        try {
            List<AiConversationResponse> history = userService.getAiHistory(username);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), history));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
        }
    }


    @PostMapping("/group/create")
    public ResponseEntity<ResultVo<String>> createGroup(@RequestBody CreateGroupRequest request) {
        try {
            userService.createGroup(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Group created successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("Member not found") || e.getMessage().equals("User not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if (e.getMessage().equals("Group name already exists")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_IS_EXIST.Code(), "群聊名称已存在"));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @PostMapping("/group/join")
    public ResponseEntity<ResultVo<String>> joinGroup(@RequestBody JoinGroupRequest request) {
        try {
            userService.joinGroup(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Joined group successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User not found") || e.getMessage().equals("Group not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if (e.getMessage().equals("User is already a member of the group")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_IS_EXIST.Code(), ResultEnums.ENTITY_IS_EXIST.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @PostMapping("/group/leave")
    public ResponseEntity<ResultVo<String>> leaveGroup(@RequestBody LeaveGroupRequest request) {
        try {
            userService.leaveGroup(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Left group successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User not found") || e.getMessage().equals("Group not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if (e.getMessage().equals("User is not a member of the group")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_NO_RECORD.Code(), ResultEnums.ERROR_NO_RECORD.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @PostMapping("/group/message/send")
    public ResponseEntity<ResultVo<String>> sendGroupMessage(@RequestBody SendGroupMessageRequest request) {
        try {
            userService.sendGroupMessage(request);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), "Group message sent successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Sender not found") || e.getMessage().equals("Group not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if (e.getMessage().equals("Sender is not a member of the group")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_NO_RECORD.Code(), ResultEnums.ERROR_NO_RECORD.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @GetMapping("/group/message/list")
    public ResponseEntity<ResultVo<List<GroupMessageResponse>>> getGroupMessages(
            @RequestParam String username, @RequestParam String groupName) {
        try {
            List<GroupMessageResponse> messages = userService.getGroupMessages(username, groupName);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), messages));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User not found") || e.getMessage().equals("Group not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else if (e.getMessage().equals("User is not a member of the group")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_NO_RECORD.Code(), ResultEnums.ERROR_NO_RECORD.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }

    @GetMapping("/group/list")
    public ResponseEntity<ResultVo<List<GroupListResponse>>> getUserGroups(@RequestParam String username) {
        try {
            List<GroupListResponse> groups = userService.getUserGroups(username);
            return ResponseEntity.ok(ResultVo.getSuccess(ResultEnums.SUCCESS.Code(), ResultEnums.SUCCESS.Desc(), groups));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ENTITY_NOT_EXIST.Code(), ResultEnums.ENTITY_NOT_EXIST.Desc()));
            } else {
                return ResponseEntity.status(400).body(ResultVo.getFail(ResultEnums.ERROR_UNKNOWN.Code(), e.getMessage()));
            }
        }
    }
}
