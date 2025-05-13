package com.example.gradwork_backend.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Data
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private LocalDateTime joinedAt;
}
