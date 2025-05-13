package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.AiConversation;
import com.example.gradwork_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiConversationRepository extends JpaRepository<AiConversation, Long> {
    List<AiConversation> findByUserOrderByTimestampAsc(User user);
}
