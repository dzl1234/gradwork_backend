package com.example.gradwork_backend.service;

import com.example.gradwork_backend.dto.AddFriendRequest;
import com.example.gradwork_backend.dto.FriendListResponse;
import com.example.gradwork_backend.dto.LoginRequest;
import com.example.gradwork_backend.dto.RegisterRequest;
import com.example.gradwork_backend.dto.RemoveFriendRequest;
import com.example.gradwork_backend.entity.Friend;
import com.example.gradwork_backend.entity.User;
import com.example.gradwork_backend.repository.FriendRepository;
import com.example.gradwork_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        userRepository.save(user);
    }

    @Transactional
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user.getUsername();
    }

    @Transactional
    public void addFriend(AddFriendRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUsername(request.getFriendUsername())
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        if (user.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        if (friendRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new IllegalArgumentException("Friend already exists");
        }

        Friend friendship = new Friend();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendRepository.save(friendship);
    }

    @Transactional
    public void removeFriend(RemoveFriendRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUsername(request.getFriendUsername())
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        if (!friendRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new IllegalArgumentException("Friendship does not exist");
        }

        friendRepository.deleteByUserAndFriend(user, friend);
    }

    @Transactional(readOnly = true)
    public FriendListResponse getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friend> friendships = friendRepository.findByUser(user);
        List<String> friendUsernames = friendships.stream()
                .map(friendship -> friendship.getFriend().getUsername())
                .collect(Collectors.toList());

        FriendListResponse response = new FriendListResponse();
        response.setFriends(friendUsernames);
        return response;
    }
}
