package org.acme.bot.domain.models;

import java.util.Date;

/**
 *
 *<p>
 * Role : initiate the role with his name to begin with then we will modify it to also have a restraint list of rights.
 *</p>
 *<ul>
 *<li>Utilizing Java Records for concise, immutable data carriers.</li>
 * </ul>
 *
 * @param name
 *
 */
public record Role(String name) {

    public Role{
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
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
        return "Channel{" + "name='" + name + '}';
    }

}
