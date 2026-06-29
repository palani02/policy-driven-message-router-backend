package com.grootan.router.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @PostConstruct
    public void initTwilio() {

        System.out.println("=================================");
        System.out.println("INITIALIZING TWILIO");
        System.out.println("=================================");

        Twilio.init(accountSid, authToken);

        System.out.println("TWILIO INITIALIZED SUCCESSFULLY");
        System.out.println("=================================");
    }
}