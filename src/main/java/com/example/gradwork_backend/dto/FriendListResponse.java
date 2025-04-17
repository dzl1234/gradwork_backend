package com.example.gradwork_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FriendListResponse {
    private List<String> friends; // 好友用户名列表
}
