package org.acme.bot.command;

import discord4j.core.object.entity.Message;
import jakarta.enterprise.context.ApplicationScoped;
import reactor.core.publisher.Mono;

/**
 *
 * Discord command "!ping" : answers "pong!" (no network call, useful to check the bot is alive).
 *
 */
@ApplicationScoped
public class PingCommandHandler implements CommandHandler {

    public static final String COMMAND = "!ping";

    static final String ANSWER = "pong!";

    @Override
    public boolean supports(String content) {
        return content != null && content.strip().equalsIgnoreCase(COMMAND);
    }

    @Override
    public Mono<Void> handle(Message message) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(ANSWER))
                .then();
    }
}