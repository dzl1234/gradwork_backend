package com.example.gradwork_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMessageResponse {
    private Long id;
    private Long groupId;
    private String senderUsername;
    private String content;
    private String translatedContent;
    private LocalDateTime sendTime;
}
