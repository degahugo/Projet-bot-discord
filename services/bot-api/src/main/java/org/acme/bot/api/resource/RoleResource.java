package org.acme.bot.api.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.bot.application.dto.RoleDTO;
import org.acme.bot.application.dto.RoleCreateRequest;
import org.acme.bot.application.services.RoleService;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 *
 * REST resource : Production-ready API with validation and documentation.
 *
 */

@Path("/api/v1/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Role", description="Validated and documented production-ready API")
public class RoleResource {

    private final RoleService service;

    @Inject
    public RoleResource(RoleService service) {
        this.service = service;
    }

    /**
     *
     * @return a list of all roles mapped to DTOs.
     *
     */

    @GET
    @Operation(summary="Get all roles", description="Returns a list of all roles")
    @APIResponse(responseCode="200",description="List of roles",content=@Content(mediaType="application/json",schema=@Schema(implementation= RoleDTO.class,type= SchemaType.ARRAY)))
    public List<RoleDTO> getall(){
        return service.getAll();
    }

    /**
     *
     * @return the role with this name, or 404 if it does not exist
     *
     */

    @GET
    @Path("/{name}")
    @Operation(summary="Get a role by its name", description="Returns the role with this name")
    @APIResponse(responseCode="200",description="Role found",content=@Content(mediaType="application/json",schema=@Schema(implementation=RoleDTO.class)))
    @APIResponse(responseCode="404",description="name not found")
    public RoleDTO getByName(@PathParam("name")String name){
        return service.getByName(name);
    }

    /**
     *
     * Create a new Role
     *
     */

    @POST
    @Operation(summary="Create a new Role",description="Validates input and persists a new role")
    @APIResponse(responseCode="201",description="Role succesfully created",content=@Content(mediaType="application/json",schema=@Schema(implementation=RoleDTO.class)))
    @APIResponse(responseCode="400",description="Invalid input or domain validation failure")
    @APIResponse(responseCode="409",description="name already exists")
    public Response create(@Valid RoleCreateRequest request){
        RoleDTO created=service.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     *
     *Delete a Role
     */

    @DELETE
    @Path("/{name}")
    @Operation(summary="Delete a role",description="Deletes the role with this name")
    @APIResponse(responseCode="204",description="Role deleted")
    @APIResponse(responseCode="404",description="name not found")
    public Response delete(@PathParam("name")String name){
        service.delete(name);
        return Response.noContent().build();
    }
}
