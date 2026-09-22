package org.acme.bot.application.dto;


import org.eclipse.microprofile.openapi.annotations.media.Schema;


/**
 *
 * Data Transfert Object representing a product in the public API
 *<ul>
 *<li> Read-only contract returnd to API consumers</li>
 *<li>Ensures API stability regardless of internal refactoring</li>
 *</ul>
 */

@Schema(description ="Role representation for public API responses")
public record RoleDTO(@Schema(description = "name", examples = { "Admin" }) String name){


}
