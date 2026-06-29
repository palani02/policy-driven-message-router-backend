package com.grootan.router.service;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;

public interface EmailService {

    void sendEmail(User user, Message message);

}