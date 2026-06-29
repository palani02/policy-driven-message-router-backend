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

    private Message message;


    private User user;


    private RoutingDecision routingDecision;

}