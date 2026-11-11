package org.acme.bot.command;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * Unit tests of the "!roles" command : no Discord, no HTTP, no Quarkus.
 *
 */
class RoleCommandHandlerTest {

    // supports() does not use the service : null is enough here
    private final RoleCommandHandler handler = new RoleCommandHandler(null);

    @Test
    void recognizesTheCommandWhateverTheCaseAndSpaces() {
        assertTrue(handler.supports("!roles"));
        assertTrue(handler.supports("  !ROLES  "));
        assertFalse(handler.supports("!role"));
        assertFalse(handler.supports("!roles Admin"));
        assertFalse(handler.supports(null));
    }

    @Test
    void listsTheRoleNames() {
        assertEquals("Rôles disponibles : Admin, Moderator",
                RoleCommandHandler.formatRoles(List.of("Admin", "Moderator")));
    }

    @Test
    void saysWhenThereIsNoRole() {
        assertEquals(RoleCommandHandler.EMPTY_MESSAGE, RoleCommandHandler.formatRoles(List.of()));
        assertEquals(RoleCommandHandler.EMPTY_MESSAGE, RoleCommandHandler.formatRoles(null));
    }

    @Test
    void neverExceedsTheDiscordLimit() {
        List<String> many = Collections.nCopies(500, "RoleTresLong");
        String response = RoleCommandHandler.formatRoles(many);
        assertEquals(RoleCommandHandler.DISCORD_MAX_LENGTH, response.length());
        assertTrue(response.endsWith("…"));
    }
}