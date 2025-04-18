package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String senderUsername;    // 发送者用户名
    private String receiverUsername;  // 接收者用户名
    private String content;          // 消息内容
}
