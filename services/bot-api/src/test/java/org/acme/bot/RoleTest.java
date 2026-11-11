package org.acme.bot;

import org.acme.bot.domain.models.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * Unit test of the Role Domain model (no Quarkus, no database)
 *
 */
class RoleTest {

    @Test
    void nameIsStripped() {
        assertEquals("Admin", Role.of("  Admin ").name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void blankNameIsRejected(String name) {
        assertThrows(IllegalArgumentException.class, () -> Role.of(name));
    }
}
