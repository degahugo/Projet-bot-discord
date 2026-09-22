package org.acme.bot.infrastructure.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.bot.infrastructure.persistence.entity.RoleEntity;

import java.util.List;
import java.util.Optional;

/**
 *
 * Panache Repository for RoleEntity
 */
@ApplicationScoped
public class RoleRepository implements PanacheRepository<RoleEntity> {

    /**
     *
     * Retrieves a Role by its unique name since a Role is define by his name
     *
     * @param name
     */

    public Optional<RoleEntity> findByName(String name) {
        return find("name",name).firstResultOptional();
    }

    /**
     *
     * Return all the Role available
     *
     * @return a list of RoleEntity
     */

    public List<RoleEntity> allRole(){
        return list("name");
    }



}
