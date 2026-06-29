package com.grootan.router.channel;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import com.grootan.router.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsChannel implements Channel {

    private final SmsService smsService;

    @Override
    public void send(User user, Message message) {

        System.out.println("=================================");
        System.out.println("SMS CHANNEL INITIATED");
        System.out.println("=================================");

        try {
            System.out.println("Sending SMS via Twilio...");
            System.out.println("To      : " + user.getPhoneNumber());
            System.out.println("Message : " + message.getContent());

            smsService.sendSms(user, message);

            System.out.println("=================================");
            System.out.println("SMS SENT SUCCESSFULLY");
            System.out.println("=================================");

        } catch (Exception ex) {

            System.err.println("=================================");
            System.err.println("SMS DELIVERY FAILED");
            System.err.println("Reason : " + ex.getMessage());
            System.err.println("=================================");

            // IMPORTANT: rethrow so retry + DLQ system still works
            throw ex;
        }
    }
}