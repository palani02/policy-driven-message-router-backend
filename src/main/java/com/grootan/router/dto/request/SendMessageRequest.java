package com.grootan.router.dto.request;

import com.grootan.router.enums.MessageType;
import com.grootan.router.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    @NotNull(message = "Priority is required")
    private Priority priority;

}