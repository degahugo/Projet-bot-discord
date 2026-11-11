package org.acme.bot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.acme.bot.command.CommandHandler;
import org.acme.bot.command.RoleCommandHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Startup
@ApplicationScoped
public class ExampleQuarkus {

    private GatewayDiscordClient gateway;

    // DISCORD_TOKEN : read by Quarkus from the .env file (dev) or from an environment variable
    @ConfigProperty(name = "discord.token")
    Optional<String> discordToken;

    // false in the tests (application.properties) : we don't connect to Discord
    @ConfigProperty(name = "discord.enabled", defaultValue = "true")
    boolean enabled;

    // CDI gives ALL the beans implementing CommandHandler (PingCommandHandler, RoleCommandHandler...)
    @Inject
    Instance<CommandHandler> commandHandlers;

    @PostConstruct
    public void start() {

        if (!enabled) {
            System.out.println("Bot Discord désactivé (discord.enabled=false)");
            return;
        }

        String token = discordToken.orElse(null);

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("DISCORD_TOKEN n'est pas défini dans le fichier .env");
        }

        DiscordClient client = DiscordClient.create(token);

        gateway = client.gateway()
                .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.MESSAGE_CONTENT)))
                .login()
                .block();

        if (gateway == null) {
            throw new IllegalStateException("Impossible de se connecter à Discord");
        }

        Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                Mono.fromRunnable(() -> {
                    User self = event.getSelf();
                    System.out.printf("Connecté en tant que %s%n", self.getUsername());
                })
        ).then();


        // Every message is sent to the first CommandHandler which supports it
        Mono<Void> handleCommands = gateway.on(MessageCreateEvent.class, this::dispatch).then();
            printOnLogin.subscribe();
            handleCommands.subscribe();
    }

    /**
     *
     * Finds the command matching the message and runs it. Unknown messages are ignored.
     *
     */
    private Mono<Void> dispatch(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (message.getAuthor().map(User::isBot).orElse(true)) {
            return Mono.empty();
        }

        String content = message.getContent().strip();

        return commandHandlers.stream()
                .filter(handler -> handler.supports(content))
                .findFirst()
                .map(handler -> handler.handle(message))
                .orElse(Mono.empty());
    }

    @PreDestroy
    public void stop() {
        if (gateway != null) {
            gateway.logout().block();
        }
    }
}
