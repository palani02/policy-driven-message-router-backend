package com.grootan.router.service.impl;

import com.grootan.router.dto.response.TrackingResponse;
import com.grootan.router.entity.Message;
import com.grootan.router.entity.MessageDelivery;
import com.grootan.router.enums.MessageStatus;
import com.grootan.router.repository.MessageDeliveryRepository;
import com.grootan.router.repository.MessageRepository;
import com.grootan.router.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final MessageRepository messageRepository;
    private final MessageDeliveryRepository messageDeliveryRepository;

    @Override
    public TrackingResponse getMessageTracking(Long messageId) {

        // Step 1: Fetch Message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found with id: " + messageId));

        // Step 2: Fetch all deliveries for this message
        List<MessageDelivery> deliveries =
                messageDeliveryRepository.findByMessage(message);

        // Step 3: Map deliveries → DTO
        List<TrackingResponse.ChannelTracking> channelTrackings =
                deliveries.stream()
                        .map(delivery -> TrackingResponse.ChannelTracking.builder()
                                .channelType(delivery.getChannelType())
                                .status(delivery.getStatus())

                                // ✅ FIX: no null check needed (int primitive)
                                .attempts(delivery.getAttemptCount())

                                .lastAttemptAt(delivery.getUpdatedAt())
                                .failureReason(delivery.getFailureReason())
                                .build())
                        .collect(Collectors.toList());

        // Step 4: Compute overall status
        MessageStatus overallStatus = resolveOverallStatus(deliveries);

        // Step 5: Build response
        return TrackingResponse.builder()
                .messageId(message.getId())
                .subject(message.getSubject())
                .overallStatus(overallStatus)
                .createdAt(message.getCreatedAt())
                .channels(channelTrackings)
                .build();
    }

    /**
     * Derives overall message status from channel-level results
     */
    private MessageStatus resolveOverallStatus(List<MessageDelivery> deliveries) {

        if (deliveries == null || deliveries.isEmpty()) {
            return MessageStatus.PROCESSING;
        }

        boolean allSent = deliveries.stream()
                .allMatch(d -> d.getStatus() == MessageStatus.SENT);

        boolean anyFailed = deliveries.stream()
                .anyMatch(d -> d.getStatus() == MessageStatus.FAILED);

        if (allSent) {
            return MessageStatus.SENT;
        }

        if (anyFailed) {
            return MessageStatus.PARTIALLY_FAILED;
        }

        return MessageStatus.PROCESSING;
    }
}