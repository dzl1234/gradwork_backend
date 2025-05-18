package com.example.gradwork_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupListResponse {
    private Long groupId;
    private String groupName;
    private LocalDateTime createdAt;
}
