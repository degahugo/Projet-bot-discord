package org.acme.bot.application.exceptions;

/**
 *
 * Thrown when we try to create a Role with a name that already exists
 * Translated into HTTP 409 (Conflict) by the ErrorMappers
 *
 */
public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException(String name) {
        super("Role already exists: " + name);
    }
}
