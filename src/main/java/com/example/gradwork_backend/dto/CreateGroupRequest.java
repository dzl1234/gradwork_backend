package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private String username; // 创建者用户名
    private String groupName; // 群聊名称
}
