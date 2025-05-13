package com.example.gradwork_backend.entity;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
@Entity
@Table(name = "groups")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    private LocalDateTime createdAt;
}
