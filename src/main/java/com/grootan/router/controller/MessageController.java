package com.grootan.router.controller;

import com.grootan.router.dto.request.SendMessageRequest;
import com.grootan.router.dto.response.MessageResponse;
import com.grootan.router.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request) {

        MessageResponse response = messageService.sendMessage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}