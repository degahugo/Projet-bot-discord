package org.acme.bot.domain.models;

import org.acme.bot.processors.annotations.GenerateEntity;
import org.acme.bot.processors.annotations.Unique;

/**
 *
 *<p>
 * Role : initiate the role with his name to begin with then we will modify it to also have a restraint list of rights.
 *</p>
 *<ul>
 *<li>Utilizing Java Records for concise, immutable data carriers.</li>
 *<li>The JPA entity RoleEntity is generated from this record by the ProcessorEntity (@GenerateEntity).</li>
 * </ul>
 *
 * @param name
 *
 */
@GenerateEntity("roles")
public record Role(@Unique String name) {

    public Role{
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        // " Admin " and "Admin" are the same role
        name = name.strip();
    }

    /**
     *
     * Factory method for object creation
     */

    public static Role of(String name) {
        return new Role(name);
    }

    @Override
    public String toString() {
        return "Role {" + "name='" + name + "'}";
    }

}
