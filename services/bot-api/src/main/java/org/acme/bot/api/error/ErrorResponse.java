package org.acme.bot.api.error;

/**
 *
 * JSON body returned when an error occurs :
 * { "status": 409, "error": "Conflict", "message": "Role already exists: Admin" }
 *
 */
public record ErrorResponse(int status, String error, String message) {
}
