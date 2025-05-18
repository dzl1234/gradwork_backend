package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.Group;
import com.example.gradwork_backend.entity.GroupMember;
import com.example.gradwork_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    List<GroupMember> findByUser(User user);
    void deleteByGroupAndUser(Group group, User user);
}
