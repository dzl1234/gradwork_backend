package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.Message;
import com.example.gradwork_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSender(
            User sender, User receiver, User receiver2, User sender2);
}
