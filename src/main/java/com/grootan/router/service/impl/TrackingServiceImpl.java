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


        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found with id: " + messageId));


        List<MessageDelivery> deliveries =
                messageDeliveryRepository.findByMessage(message);


        List<TrackingResponse.ChannelTracking> channelTrackings =
                deliveries.stream()
                        .map(delivery -> TrackingResponse.ChannelTracking.builder()
                                .channelType(delivery.getChannelType())
                                .status(delivery.getStatus())


                                .attempts(delivery.getAttemptCount())

                                .lastAttemptAt(delivery.getUpdatedAt())
                                .failureReason(delivery.getFailureReason())
                                .build())
                        .collect(Collectors.toList());


        MessageStatus overallStatus = resolveOverallStatus(deliveries);


        return TrackingResponse.builder()
                .messageId(message.getId())
                .subject(message.getSubject())
                .overallStatus(overallStatus)
                .createdAt(message.getCreatedAt())
                .channels(channelTrackings)
                .build();
    }


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