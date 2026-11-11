package org.acme.bot.application.exceptions;

/**
 *
 * Thrown when a model is not found (ex : new ModelNotFoundException("Role", "Admin"))
 * Translated into HTTP 404 (Not Found) by the ErrorMappers
 *
 */
public class ModelNotFoundException extends RuntimeException {

    public ModelNotFoundException(String model, Object key) {
        super(model + " not found: " + key);
    }
}