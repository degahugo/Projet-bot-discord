package org.acme.bot.application.exceptions;

/**
 *
 * Thrown when we try to create a model whose unique key already exists
 * (ex : new ModelAlreadyExistsException("Role", "Admin"))
 * Translated into HTTP 409 (Conflict) by the ErrorMappers
 *
 */
public class ModelAlreadyExistsException extends RuntimeException {

    public ModelAlreadyExistsException(String model, Object key) {
        super(model + " already exists: " + key);
    }
}