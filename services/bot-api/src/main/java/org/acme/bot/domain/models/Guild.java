package org.acme.bot.domain.models;


import org.acme.bot.processors.annotations.*;

/**
 *
 * <p>
 *     Guild : initiate the Guild with his name
 * </p>
 * <ul>
 *     <li>Utilizing Java Record for concise, immutable data carriers</li>
 * </ul>
 *
 * @param name
 */
@GenerateEntity("guild")
@GenerateRepository
@GenerateMapper
@GenerateService
@GenerateResource
public record Guild(@Unique @DTO("DTO") @DTO("CreateRequest") String name) {
    public Guild{
        if(name== null){
            throw new IllegalArgumentException("name cannot be null or empty");
        }
    }

    /**
     *
     * Factory method for object creation
     *
     */

    public static Guild of(String name){
        return new Guild(name);
    }

    @Override
    public String toString() {
        return "Guild {"+ "name='" + name + '}';
    }
}

