package com.example.gradwork_backend.service;

import com.example.gradwork_backend.dto.*;
import com.example.gradwork_backend.entity.*;
import com.example.gradwork_backend.repository.*;
import com.example.gradwork_backend.util.BaiduTranslateClient;
import com.example.gradwork_backend.util.SparkDeskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AiConversationRepository aiConversationRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private BaiduTranslateClient baiduTranslateClient;

    @Autowired
    private SparkDeskClient sparkDeskClient;
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
        // 从朋友仓库中查找该用户的所有朋友
        List<Friend> friendships = friendRepository.findByUser(user);
        // 将朋友列表转换为用户名列表
        List<String> friendUsernames = friendships.stream()
                .map(friendship -> friendship.getFriend().getUsername())
                .collect(Collectors.toList());
        // 创建响应对象
        FriendListResponse response = new FriendListResponse();
        // 设置朋友列表到响应对象中
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
        // 通过用户和好友查找消息
        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSender(user, friend);
        // 将消息转换为响应对象并返回
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
    @Transactional
    public String askAi(String question, String username) { // 添加 username 参数
        try {
            // 查找用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // 调用星火 AI 获取回答
            String answer = sparkDeskClient.ask(question);

            // 保存问答记录
            AiConversation conversation = new AiConversation();
            conversation.setUser(user);
            conversation.setQuestion(question);
            conversation.setAnswer(answer);
            conversation.setTimestamp(LocalDateTime.now());
            aiConversationRepository.save(conversation);

            return answer;
        } catch (Exception e) {
            throw new IllegalArgumentException("AI request failed: " + e.getMessage());
        }
    }

    public String askAi(String question) {
        try {
            return sparkDeskClient.ask(question);
        } catch (Exception e) {
            throw new IllegalArgumentException("AI request failed: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<AiConversationResponse> getAiHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<AiConversation> conversations = aiConversationRepository.findByUserOrderByTimestampAsc(user);
        return conversations.stream().map(conversation -> {
            AiConversationResponse response = new AiConversationResponse();
            response.setId(conversation.getId());
            response.setQuestion(conversation.getQuestion());
            response.setAnswer(conversation.getAnswer());
            response.setTimestamp(conversation.getTimestamp());
            return response;
        }).collect(Collectors.toList());
    }
    public void createGroup(CreateGroupRequest request) {
        // 检查群聊名称是否已存在
        if (groupRepository.findByName(request.getGroupName()).isPresent()) {
            throw new IllegalArgumentException("Group name already exists");
        }

        User creator = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Group group = new Group();
        group.setName(request.getGroupName());
        group.setCreatedAt(LocalDateTime.now());
        groupRepository.save(group);

        // 仅添加创建者作为群成员
        GroupMember creatorMember = new GroupMember();
        creatorMember.setGroup(group);
        creatorMember.setUser(creator);
        groupMemberRepository.save(creatorMember);
    }


    @Transactional
    public void joinGroup(JoinGroupRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Group group = groupRepository.findByName(request.getGroupName())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (groupMemberRepository.findByGroupAndUser(group, user).isPresent()) {
            throw new IllegalArgumentException("User is already a member of the group");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void leaveGroup(LeaveGroupRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (groupMemberRepository.findByGroupAndUser(group, user).isEmpty()) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        groupMemberRepository.deleteByGroupAndUser(group, user);
    }

    @Transactional
    public void sendGroupMessage(SendGroupMessageRequest request) {
        User sender = userRepository.findByUsername(request.getSenderUsername())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        Group group = groupRepository.findByName(request.getGroupName())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (groupMemberRepository.findByGroupAndUser(group, sender).isEmpty()) {
            throw new IllegalArgumentException("Sender is not a member of the group");
        }

        String translatedContent;
        try {
            translatedContent = baiduTranslateClient.translateToEnglish(request.getContent());
        } catch (Exception e) {
            throw new IllegalArgumentException("Translation failed: " + e.getMessage());
        }

        GroupMessage message = new GroupMessage();
        message.setGroup(group);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setTranslatedContent(translatedContent);
        message.setSendTime(LocalDateTime.now());
        groupMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<GroupMessageResponse> getGroupMessages(String username, String groupName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (groupMemberRepository.findByGroupAndUser(group, user).isEmpty()) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        List<GroupMessage> messages = groupMessageRepository.findByGroupOrderBySendTimeAsc(group);
        return messages.stream().map(message -> {
            GroupMessageResponse response = new GroupMessageResponse();
            response.setId(message.getId());
            response.setGroupId(message.getGroup().getId());
            response.setSenderUsername(message.getSender().getUsername());
            response.setContent(message.getContent());
            response.setTranslatedContent(message.getTranslatedContent());
            response.setSendTime(message.getSendTime());
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupListResponse> getUserGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<GroupMember> memberships = groupMemberRepository.findByUser(user);
        return memberships.stream().map(membership -> {
            GroupListResponse response = new GroupListResponse();
            response.setGroupId(membership.getGroup().getId());
            response.setGroupName(membership.getGroup().getName());
            response.setCreatedAt(membership.getGroup().getCreatedAt());
            return response;
        }).collect(Collectors.toList());
    }
}
