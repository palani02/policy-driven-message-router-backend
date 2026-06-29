package com.grootan.router.service.impl;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import com.grootan.router.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(User user, Message message) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(user.getEmail());
        mail.setSubject(message.getSubject());
        mail.setText(message.getContent());

        mailSender.send(mail);

    }

}