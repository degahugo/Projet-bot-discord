package org.acme.bot.infrastructure.mapper;


import org.acme.bot.application.dto.RoleDTO;
import org.acme.bot.application.dto.usecases.CreateRoleRequest;
import org.acme.bot.domain.models.Role;
import org.acme.bot.infrastructure.persistence.entity.RoleEntity;

/**
 *
 *Component responsible for transforming data between architectural layers
 *
 */

public final class RoleMapper {
    private RoleMapper(){
        // Prevent instantiation of utility class
    }

    /**
     *
     * Maps a Persistence Entity to an API Response DTO
     * Used for outbound data (GET requests)
     *
     */

    public static RoleDTO toDto(RoleEntity entity){
        return new RoleDTO(entity.getName());
    }

    /**
     *
     *Maps an API Request DTO to a Persistence Entity via the Domain model
     *
     */

    public static RoleEntity toEntity(CreateRoleRequest request){
        // Step 1 : Create a Domain Object (Domain Rule Validation)
        var domain = Role.of(request.name());

        //Step 2 : Convert to Persistence Entity
        // If execution reaches here, the data is guaranteed to be valid
        return new RoleEntity(domain.name());
    }
}
