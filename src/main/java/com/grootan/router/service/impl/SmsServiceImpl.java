package com.grootan.router.service.impl;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import com.grootan.router.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;


    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);

        System.out.println("=================================");
        System.out.println("TWILIO INITIALIZED");
        System.out.println("ACCOUNT SID LOADED");
        System.out.println("=================================");
    }

    @Override
    public void sendSms(User user, Message message) {

        System.out.println("=================================");
        System.out.println("REAL SMS SERVICE (TWILIO)");
        System.out.println("=================================");

        try {
            String to = formatPhoneNumber(user.getPhoneNumber());

            System.out.println("From    : " + twilioPhoneNumber);
            System.out.println("To      : " + to);
            System.out.println("Message : " + message.getContent());

            MessageCreator creator = com.twilio.rest.api.v2010.account.Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioPhoneNumber),
                    message.getContent()
            );

            creator.create();

            System.out.println("=================================");
            System.out.println("SMS SENT SUCCESSFULLY VIA TWILIO");
            System.out.println("=================================");

        } catch (Exception ex) {

            System.err.println("=================================");
            System.err.println("TWILIO SMS FAILED");
            System.err.println("Reason: " + ex.getMessage());
            System.err.println("=================================");

            throw new RuntimeException("SMS sending failed via Twilio", ex);
        }
    }

    private String formatPhoneNumber(String phone) {

        if (phone == null) {
            throw new RuntimeException("Phone number is null");
        }

        phone = phone.trim();

        if (phone.startsWith("+")) {
            return phone;
        }


        return "+91" + phone;
    }
}