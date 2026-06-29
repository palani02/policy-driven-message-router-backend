package com.grootan.router.channel;

import com.grootan.router.enums.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class ChannelRegistry {

    private final EmailChannel emailChannel;
    private final SmsChannel smsChannel;




    public ChannelRegistry(EmailChannel emailChannel,
                           SmsChannel smsChannel) {

        this.emailChannel = emailChannel;
        this.smsChannel = smsChannel;
    }

    public Channel getChannel(ChannelType channelType) {

        return switch (channelType) {

            case EMAIL -> emailChannel;

            case SMS -> smsChannel;

        };

    }

}