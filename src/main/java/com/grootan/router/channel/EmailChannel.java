package com.grootan.router.channel;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import com.grootan.router.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailChannel implements Channel {

    private final EmailService emailService;

    @Override
    public void send(User user, Message message) {

        System.out.println("=================================");
        System.out.println("EMAIL CHANNEL");
        System.out.println("Sending Email...");
        System.out.println("=================================");

        emailService.sendEmail(user, message);

        System.out.println("=================================");
        System.out.println("EMAIL SENT SUCCESSFULLY");
        System.out.println("=================================");

    }

}