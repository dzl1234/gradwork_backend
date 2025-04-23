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
import java.time.LocalDateTime;
import com.example.gradwork_backend.util.BaiduTranslateClient;
import com.example.gradwork_backend.repository.MessageRepository;
import com.example.gradwork_backend.dto.MessageResponse;
import com.example.gradwork_backend.dto.SendMessageRequest;
import com.example.gradwork_backend.entity.Message;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BaiduTranslateClient baiduTranslateClient;
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

    @Transactional
    public void sendMessage(SendMessageRequest request) {
        User sender = userRepository.findByUsername(request.getSenderUsername())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findByUsername(request.getReceiverUsername())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // 验证是否为好友
        if (!friendRepository.findByUserAndFriend(sender, receiver).isPresent()) {
            throw new IllegalArgumentException("Receiver is not a friend");
        }

        // 翻译消息内容
        String translatedContent;
        try {
            translatedContent = baiduTranslateClient.translateToEnglish(request.getContent());
        } catch (Exception e) {
            throw new IllegalArgumentException("Translation failed: " + e.getMessage());
        }

        // 保存消息
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setTranslatedContent(translatedContent);
        message.setSendTime(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(String username, String friendUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSender(user, friend);

        return messages.stream().map(message -> {
            MessageResponse response = new MessageResponse();
            response.setId(message.getId());
            response.setSenderUsername(message.getSender().getUsername());
            response.setReceiverUsername(message.getReceiver().getUsername());
            response.setContent(message.getContent());
            response.setTranslatedContent(message.getTranslatedContent());
            response.setSendTime(message.getSendTime());
            return response;
        }).collect(Collectors.toList());
    }
}
