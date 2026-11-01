package org.acme.bot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import io.github.cdimascio.dotenv.Dotenv;
import reactor.core.publisher.Mono;

public class Example {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("DISCORD_TOKEN n'est pas défini dans le fichier .env");
        }

        DiscordClient client = DiscordClient.create(token);

        Mono<Void> login = client.gateway()
                // Active l'intent privilégié MESSAGE_CONTENT
                .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.MESSAGE_CONTENT)))
                .withGateway((GatewayDiscordClient gateway) -> {

                    // Affiche un message quand le bot est connecté
                    Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                User self = event.getSelf();
                                System.out.printf("Connecté en tant que %s%n", self.getUsername());
                            })
                    ).then();

                    // Répond "pong!" à "!ping"
                    Mono<Void> handlePing = gateway.on(MessageCreateEvent.class, event -> {
                        Message message = event.getMessage();

                        // Ignore les messages des bots (dont le sien)
                        if (message.getAuthor().map(User::isBot).orElse(true)) {
                            return Mono.empty();
                        }

                        if (message.getContent().equalsIgnoreCase("!ping")) {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("pong!"));
                        }
                        return Mono.empty();
                    }).then();

                    // Combine les deux : le Mono ne se termine jamais tant que le bot tourne
                    return printOnLogin.and(handlePing);
                });

        login.block();
    }
}