package org.acme.bot.command;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

/**
 *
 * A Discord command (ex : "!ping", "!roles").
 * <p>
 * Every @ApplicationScoped class implementing this interface is a CDI bean :
 * ExampleQuarkus finds them all with Instance&lt;CommandHandler&gt;.
 * To add a command, create a new implementation : nothing else to change.
 * </p>
 */
public interface CommandHandler {

    /**
     *
     * @param content the content of the Discord message (already stripped)
     * @return true if this handler must answer this message
     */
    boolean supports(String content);

    /**
     *
     * @param message the Discord message which contains the command
     * @return a Mono completed when the answer has been sent
     */
    Mono<Void> handle(Message message);
}