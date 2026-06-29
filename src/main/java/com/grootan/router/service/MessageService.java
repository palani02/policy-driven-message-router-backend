package com.grootan.router.service;

import com.grootan.router.dto.request.SendMessageRequest;
import com.grootan.router.dto.response.MessageResponse;

public interface MessageService {

    MessageResponse sendMessage(SendMessageRequest request);

}