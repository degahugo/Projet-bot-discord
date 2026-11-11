package org.acme.bot.application;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.bot.client.api.RoleApi;
import org.acme.bot.client.model.RoleDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Objects;

/**
 *
 * Application layer of the bot for the roles.
 * <p>
 * It is the ONLY class which knows the generated client (RoleApi, RoleDTO) :
 * it converts the contract of bot-api into simple types for the Discord commands.
 * If the contract changes, only this class may need to change.
 * </p>
 */
@ApplicationScoped
public class RoleDiscordService {

    private final RoleApi client;

    public RoleDiscordService(@RestClient RoleApi client) {
        this.client = client;
    }

    /**
     *
     * Blocking call to bot-api (GET /api/v1/roles).
     *
     * @return the names of the roles stored by bot-api (empty list if there is none)
     */
    public List<String> getRoleNames() {
        List<RoleDTO> roles = client.getAllRole();
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(RoleDTO::getName)
                .filter(Objects::nonNull)
                .toList();
    }
}