package com.grootan.router.service.impl;

import com.grootan.router.dto.request.SendMessageRequest;
import com.grootan.router.dto.response.MessageResponse;
import com.grootan.router.engine.RoutingEngine;
import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import com.grootan.router.entity.UserPreference;
import com.grootan.router.enums.MessageStatus;
import com.grootan.router.exception.ResourceNotFoundException;
import com.grootan.router.model.QueuedMessage;
import com.grootan.router.model.RoutingDecision;
import com.grootan.router.queue.MessageQueue;
import com.grootan.router.repository.MessageRepository;
import com.grootan.router.repository.UserPreferenceRepository;
import com.grootan.router.repository.UserRepository;
import com.grootan.router.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final MessageRepository messageRepository;
    private final RoutingEngine routingEngine;
    private final MessageQueue messageQueue;

    @Override
    public MessageResponse sendMessage(SendMessageRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + request.getUserId()));

        UserPreference preference =
                userPreferenceRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User preference not found for user id : "
                                                + user.getId()));

        Message message = Message.builder()
                .user(user)
                .subject(request.getSubject())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .priority(request.getPriority())
                .status(MessageStatus.CREATED)
                .build();

        RoutingDecision routingDecision =
                routingEngine.route(message, preference);

        Message savedMessage = messageRepository.save(message);

        QueuedMessage queuedMessage = QueuedMessage.builder()
                .message(savedMessage)
                .user(user)
                .routingDecision(routingDecision)
                .build();

        messageQueue.enqueue(queuedMessage);

        return MessageResponse.builder()
                .id(savedMessage.getId())
                .userId(user.getId())
                .subject(savedMessage.getSubject())
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .priority(savedMessage.getPriority())
                .status(savedMessage.getStatus())
                .build();
    }
}