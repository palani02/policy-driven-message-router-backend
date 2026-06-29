package com.grootan.router.engine;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.UserPreference;
import com.grootan.router.enums.ChannelType;
import com.grootan.router.enums.MessageType;
import com.grootan.router.model.RoutingDecision;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RulesEngine {

    public RoutingDecision evaluate(Message message, UserPreference preference) {

        List<ChannelType> channels = new ArrayList<>();
        MessageType messageType = message.getMessageType();

        /*
         * Rule 1
         * Critical Alerts
         * Always EMAIL + SMS
         */
        if (messageType == MessageType.CRITICAL) {
            channels.add(ChannelType.EMAIL);
            channels.add(ChannelType.SMS);

            return RoutingDecision.builder()
                    .channels(channels)
                    .build();
        }

        /*
         * Rule 2
         * Promotions
         * Only EMAIL
         */
        if (messageType == MessageType.PROMOTION) {
            channels.add(ChannelType.EMAIL);

            return RoutingDecision.builder()
                    .channels(channels)
                    .build();
        }

        /*
         * Rule 3
         * OTP
         * Prefer SMS
         */
        if (messageType == MessageType.OTP) {
            channels.add(ChannelType.SMS);

            return RoutingDecision.builder()
                    .channels(channels)
                    .build();
        }

        /*
         * Rule 4
         * Reminder / General
         * Respect User Preferences
         */
        if (preference.getEmailEnabled()) {
            channels.add(ChannelType.EMAIL);
        }

        if (preference.getSmsEnabled()) {
            channels.add(ChannelType.SMS);
        }

        /*
         * Rule 5
         * Time of Day (Basic Example)
         * If no channel selected and it's daytime,
         * use EMAIL as fallback.
         */
        if (channels.isEmpty()) {

            LocalTime now = LocalTime.now();

            if (now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(20, 0))) {
                channels.add(ChannelType.EMAIL);
            } else {
                channels.add(ChannelType.SMS);
            }
        }

        return RoutingDecision.builder()
                .channels(channels)
                .build();
    }

}
