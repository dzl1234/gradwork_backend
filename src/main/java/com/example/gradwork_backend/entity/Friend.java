package com.example.gradwork_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "friends", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
@Data
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;
}
