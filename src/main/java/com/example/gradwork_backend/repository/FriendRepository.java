package com.example.gradwork_backend.repository;

import com.example.gradwork_backend.entity.Friend;
import com.example.gradwork_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByUserAndFriend(User user, User friend);
    List<Friend> findByUser(User user);
    void deleteByUserAndFriend(User user, User friend);
}
