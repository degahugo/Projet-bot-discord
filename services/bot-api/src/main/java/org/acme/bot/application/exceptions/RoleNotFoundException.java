package org.acme.bot.application.exceptions;

/**
 *
 * Thrown when a Role is not found
 * Translated into HTTP 404 (Not Found) by the ErrorMappers
 *
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String name) {
        super("Role not found: " + name);
    }
}
