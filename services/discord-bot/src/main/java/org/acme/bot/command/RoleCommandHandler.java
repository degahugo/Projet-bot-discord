package org.acme.bot.command;

import discord4j.core.object.entity.Message;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.bot.application.RoleDiscordService;
import org.jboss.logging.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 *
 * Discord command "!roles" : lists the roles stored in bot-api.
 * <p>
 * Flow : Discord message -> RoleDiscordService -> generated REST client (HTTP GET) -> bot-api -> PostgreSQL,
 * then the answer is sent back in the same Discord channel.
 * </p>
 */
@ApplicationScoped
public class RoleCommandHandler implements CommandHandler {

    public static final String COMMAND = "!roles";

    static final String EMPTY_MESSAGE = "Aucun rôle disponible.";
    static final String API_ERROR_MESSAGE = "Impossible de récupérer les rôles : l'API ne répond pas. Réessaie plus tard.";

    /**
     * A Discord message cannot exceed 2000 characters
     */
    static final int DISCORD_MAX_LENGTH = 2000;

    private static final Logger LOG = Logger.getLogger(RoleCommandHandler.class);

    private final RoleDiscordService roleService;

    public RoleCommandHandler(RoleDiscordService roleService) {
        this.roleService = roleService;
    }

    @Override
    public boolean supports(String content) {
        return content != null && content.strip().equalsIgnoreCase(COMMAND);
    }

    /**
     *
     * The HTTP call is blocking : it runs on boundedElastic (a thread pool made for blocking work)
     * so that the Discord4J threads are never blocked. If bot-api is down, the bot stays
     * connected and answers an error message instead.
     *
     */
    @Override
    public Mono<Void> handle(Message message) {
        return Mono.fromCallable(roleService::getRoleNames)
                .subscribeOn(Schedulers.boundedElastic())
                .map(RoleCommandHandler::formatRoles)
                .onErrorResume(error -> {
                    LOG.warnf(error, "Appel à bot-api impossible pour %s", COMMAND);
                    return Mono.just(API_ERROR_MESSAGE);
                })
                .flatMap(response -> message.getChannel()
                        .flatMap(channel -> channel.createMessage(response)))
                .then();
    }

    /**
     *
     * Builds the text sent on Discord. Pure function : tested without Discord nor HTTP.
     *
     * @param roleNames the names of the roles returned by bot-api
     * @return "Rôles disponibles : Admin, Moderator" or a message if there is no role
     */
    static String formatRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return EMPTY_MESSAGE;
        }

        String response = "Rôles disponibles : " + String.join(", ", roleNames);

        if (response.length() > DISCORD_MAX_LENGTH) {
            return response.substring(0, DISCORD_MAX_LENGTH - 1) + "…";
        }
        return response;
    }
}