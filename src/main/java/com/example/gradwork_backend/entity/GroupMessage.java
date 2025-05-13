package com.example.gradwork_backend.entity;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
@Entity
@Table(name = "group_messages")
@Data
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    private String content;
    private String translatedContent;
    private LocalDateTime sendTime;
    private Boolean isRead;
    private Boolean isDeleted;
}
