package com.example.gradwork_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiConversationResponse {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
}
