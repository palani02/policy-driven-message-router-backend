package com.grootan.router.channel;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;

public interface Channel {

    void send(User user, Message message);

}