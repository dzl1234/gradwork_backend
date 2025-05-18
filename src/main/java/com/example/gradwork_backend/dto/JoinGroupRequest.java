package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class JoinGroupRequest {
    private String username; // 加入者用户名
    private String groupName; // 群聊名称
}
