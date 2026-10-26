package org.acme.bot.api.error;

import jakarta.ws.rs.core.Response;
import org.acme.bot.application.exceptions.RoleAlreadyExistsException;
import org.acme.bot.application.exceptions.RoleNotFoundException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 *
 * Error handling : translates the Java exceptions into HTTP responses
 * Without it, every exception becomes an error 500
 * (the errors of @Valid / @NotBlank are already translated into 400 by Quarkus)
 *
 */
public class ErrorMappers {

    /**
     *
     * Role not found -> 404
     *
     */
    @ServerExceptionMapper
    public Response notFound(RoleNotFoundException e) {
        return error(Response.Status.NOT_FOUND, e.getMessage());
    }

    /**
     *
     * Role already exists -> 409
     *
     */
    @ServerExceptionMapper
    public Response conflict(RoleAlreadyExistsException e) {
        return error(Response.Status.CONFLICT, e.getMessage());
    }

    /**
     *
     * A rule of the Domain model is not respected -> 400
     *
     */
    @ServerExceptionMapper
    public Response badRequest(IllegalArgumentException e) {
        return error(Response.Status.BAD_REQUEST, e.getMessage());
    }

    private static Response error(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), message))
                .build();
    }
}
