package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class LeaveGroupRequest {
    private String username; // 退出者用户名
    private Long groupId; // 群聊 ID
}
