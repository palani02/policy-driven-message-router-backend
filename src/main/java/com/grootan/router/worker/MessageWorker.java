package com.grootan.router.worker;

import com.grootan.router.channel.Channel;
import com.grootan.router.channel.ChannelRegistry;
import com.grootan.router.entity.DeadLetterMessage;
import com.grootan.router.entity.Message;
import com.grootan.router.entity.MessageDelivery;
import com.grootan.router.entity.User;
import com.grootan.router.enums.ChannelType;
import com.grootan.router.enums.MessageStatus;
import com.grootan.router.model.QueuedMessage;
import com.grootan.router.model.RoutingDecision;
import com.grootan.router.queue.MessageQueue;
import com.grootan.router.repository.DeadLetterRepository;
import com.grootan.router.repository.MessageDeliveryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MessageWorker {

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final MessageQueue messageQueue;
    private final ChannelRegistry channelRegistry;
    private final MessageDeliveryRepository messageDeliveryRepository;
    private final DeadLetterRepository deadLetterRepository;

    @PostConstruct
    public void startWorker() {

        Thread workerThread = new Thread(() -> {

            System.out.println("======================================");
            System.out.println("MESSAGE WORKER STARTED...");
            System.out.println("======================================");

            while (true) {

                try {

                    QueuedMessage queuedMessage = messageQueue.dequeue();

                    Message message = queuedMessage.getMessage();
                    User user = queuedMessage.getUser();
                    RoutingDecision routingDecision = queuedMessage.getRoutingDecision();

                    System.out.println("---------------------------------------");
                    System.out.println("Processing Message ID : " + message.getId());
                    System.out.println("---------------------------------------");

                    for (ChannelType channelType : routingDecision.getChannels()) {

                        boolean success = processChannel(
                                channelType,
                                user,
                                message
                        );


                        if (!success && channelType == ChannelType.SMS) {

                            boolean emailAlreadyRequested =
                                    routingDecision.getChannels()
                                            .contains(ChannelType.EMAIL);

                            if (!emailAlreadyRequested) {

                                System.out.println("======================================");
                                System.out.println("SMS FAILED");
                                System.out.println("Executing EMAIL FALLBACK...");
                                System.out.println("======================================");

                                processChannel(
                                        ChannelType.EMAIL,
                                        user,
                                        message
                                );
                            }
                        }
                    }

                    System.out.println("---------------------------------------");
                    System.out.println("MESSAGE PROCESSED SUCCESSFULLY");
                    System.out.println("Message ID : " + message.getId());
                    System.out.println("---------------------------------------");

                } catch (Exception ex) {

                    System.err.println("---------------------------------------");
                    System.err.println("WORKER ERROR");
                    ex.printStackTrace();
                    System.err.println("---------------------------------------");

                }

            }

        });

        workerThread.setDaemon(true);
        workerThread.start();
    }


    private boolean processChannel(
            ChannelType channelType,
            User user,
            Message message) {

        Channel channel = channelRegistry.getChannel(channelType);

        MessageDelivery delivery = MessageDelivery.builder()
                .message(message)
                .channelType(channelType)
                .status(MessageStatus.PROCESSING)
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        delivery = messageDeliveryRepository.save(delivery);

        boolean success = sendWithRetry(
                channel,
                user,
                message,
                channelType,
                delivery
        );

        if (success) {
            delivery.setStatus(MessageStatus.SENT);
        } else {
            delivery.setStatus(MessageStatus.FAILED);
        }

        delivery.setUpdatedAt(LocalDateTime.now());

        messageDeliveryRepository.save(delivery);

        return success;
    }


    private boolean sendWithRetry(
            Channel channel,
            User user,
            Message message,
            ChannelType channelType,
            MessageDelivery delivery) {

        int attempt = 1;

        while (attempt <= MAX_RETRY) {

            try {

                System.out.println(
                        "Attempt " + attempt + " using " + channelType);

                channel.send(user, message);

                System.out.println(
                        channelType + " delivered successfully.");

                delivery.setAttemptCount(attempt);
                delivery.setStatus(MessageStatus.SENT);
                delivery.setUpdatedAt(LocalDateTime.now());

                messageDeliveryRepository.save(delivery);

                return true;

            } catch (Exception ex) {

                System.err.println(
                        channelType
                                + " failed on attempt "
                                + attempt
                                + ". Reason: "
                                + ex.getMessage());

                delivery.setAttemptCount(attempt);
                delivery.setStatus(MessageStatus.FAILED);
                delivery.setFailureReason(ex.getMessage());
                delivery.setUpdatedAt(LocalDateTime.now());

                messageDeliveryRepository.save(delivery);

                attempt++;

                if (attempt <= MAX_RETRY) {

                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }


        DeadLetterMessage deadLetter = DeadLetterMessage.builder()
                .messageDelivery(delivery)
                .reason(delivery.getFailureReason())
                .failedAt(LocalDateTime.now())
                .build();

        deadLetterRepository.save(deadLetter);

        System.err.println("---------------------------------------");
        System.err.println("PERMANENT FAILURE");
        System.err.println("Message ID : " + message.getId());
        System.err.println("Channel    : " + channelType);
        System.err.println("Retries Exhausted.");
        System.err.println("Saved to Dead Letter Queue.");
        System.err.println("---------------------------------------");

        return false;
    }
}