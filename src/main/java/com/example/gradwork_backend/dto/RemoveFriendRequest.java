package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class RemoveFriendRequest {
    private String username;    // 当前用户
    private String friendUsername;  // 好友用户名
}
