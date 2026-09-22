package org.acme.bot.application.dto.usecases;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 *DTO used for role creation requests
 */

@Schema(description="Request object to create a new product")
public record CreateRoleRequest(@Schema(description="Name of the role",examples={"Admin"})
                                @NotBlank(message="Name is required")
                                String name)
{ }
