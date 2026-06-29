package com.grootan.router.dto.response;

import com.grootan.router.enums.MessageStatus;
import com.grootan.router.enums.MessageType;
import com.grootan.router.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private Long id;

    private Long userId;

    private String subject;

    private String content;

    private MessageType messageType;

    private Priority priority;

    private MessageStatus status;

}