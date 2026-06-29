package com.grootan.router.engine;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.UserPreference;
import com.grootan.router.model.RoutingDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoutingEngine {

    private final RulesEngine rulesEngine;

    public RoutingDecision route(Message message,
                                 UserPreference preference) {

        return rulesEngine.evaluate(message, preference);

    }

}