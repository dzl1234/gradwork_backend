package com.example.gradwork_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private String translatedContent;
    private LocalDateTime sendTime;
}
