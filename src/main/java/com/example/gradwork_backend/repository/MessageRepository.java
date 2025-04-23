package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.Message;
import com.example.gradwork_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender = :sender AND m.receiver = :receiver) OR (m.sender = :receiver AND m.receiver = :sender) ORDER BY m.sendTime ASC")
    List<Message> findBySenderAndReceiverOrReceiverAndSender(
            @Param("sender") User sender,
            @Param("receiver") User receiver);
}
