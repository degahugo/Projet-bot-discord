package org.acme.bot.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PingCommandHandlerTest {

    private final PingCommandHandler handler = new PingCommandHandler();

    @Test
    void recognizesTheCommandWhateverTheCaseAndSpaces() {
        assertTrue(handler.supports("!ping"));
        assertTrue(handler.supports("  !PING "));
        assertFalse(handler.supports("!pong"));
        assertFalse(handler.supports("!roles"));
        assertFalse(handler.supports(null));
    }
}