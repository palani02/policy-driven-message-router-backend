package com.grootan.router.dto.response;

import com.grootan.router.enums.ChannelType;
import com.grootan.router.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {

    private Long messageId;

    private String subject;

    private MessageStatus overallStatus;

    private LocalDateTime createdAt;

    private List<ChannelTracking> channels;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelTracking {

        private ChannelType channelType;

        private MessageStatus status;

        private int attempts;

        private LocalDateTime lastAttemptAt;

        private String failureReason;
    }
}