package com.grootan.router.service;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;

public interface SmsService {

    void sendSms(User user, Message message);

}