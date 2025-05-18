package com.example.gradwork_backend.dto;

import lombok.Data;

@Data
public class SendGroupMessageRequest {
    private String senderUsername; // 发送者用户名
    private String groupName; // 群聊名称
    private String content; // 消息内容
}
