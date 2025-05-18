package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.Group;
import com.example.gradwork_backend.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroupOrderBySendTimeAsc(Group group);
}
