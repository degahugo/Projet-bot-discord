package org.acme.bot.domain.models;


/**
 *
 * <p>
 *     Channel : initiate the channel with his name
 * </p>
 * <ul>
 *     <li>Utilizing Java Record for concise, immutable data carriers</li>
 * </ul>
 *
 * @param name
 */
public record Channel(String name) {

    public Channel{
        if(name== null){
            throw new IllegalArgumentException("name cannot be null or empty");
        }
    }

    /**
     *
     * Factory method for object creation
     *
     */

    public static Channel of(String name){
        return new Channel(name);
    }

    @Override
    public String toString() {
        return "Channel {"+ "name='" + name + '}';
    }
}
