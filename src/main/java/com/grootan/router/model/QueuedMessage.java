package com.grootan.router.model;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueuedMessage {

    /**
     * Saved message from database.
     */
    private Message message;

    /**
     * User who owns the message.
     */
    private User user;

    /**
     * Routing decision already calculated.
     * Worker will use this directly.
     */
    private RoutingDecision routingDecision;

}