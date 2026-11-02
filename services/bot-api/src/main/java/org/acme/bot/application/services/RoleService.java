package org.acme.bot.application.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.bot.application.dto.RoleDTO;
import org.acme.bot.application.dto.RoleCreateRequest;
import org.acme.bot.application.exceptions.RoleAlreadyExistsException;
import org.acme.bot.application.exceptions.RoleNotFoundException;
import org.acme.bot.infrastructure.mapper.RoleMapper;
import org.acme.bot.infrastructure.persistence.entity.RoleEntity;
import org.acme.bot.infrastructure.persistence.repository.RoleRepository;

import java.util.List;

/**
 *
 * Service of the Role Model
 *
 */
@ApplicationScoped
public class RoleService {

    private final RoleRepository repository;

    public RoleService(RoleRepository repository){
        this.repository = repository;

    }

    /**
     *
     * Retrieves all roles
     *
     */

    public List<RoleDTO> getAll(){
        return repository.listAll().stream()
                .map(RoleMapper::toDto).toList();
    }

    /**
     *
     * Retrieves a Role by its name
     *
     */

    public RoleDTO getByName(String name){
        return repository.findByName(name)
                .map(RoleMapper::toDto)
                .orElseThrow(()-> new RoleNotFoundException(name));
    }

    /**
     *
     * Create a Role using business logic
     *
     */

    @Transactional
    public RoleDTO create(RoleCreateRequest request){
        //Step 1 : Mapping (Validation happens inside Mapper/Domain Record)
        RoleEntity entity=RoleMapper.toEntity(request);

        //Step 2 : Integrity Rule (Using our own business Exceptions -> HTTP 409)
        if(repository.findByName(entity.getName()).isPresent()){
            throw new RoleAlreadyExistsException(entity.getName());
        }

        //Step 3 :  Persistence via Panache
        repository.persist(entity);
        return RoleMapper.toDto(entity);
    }

    /**
     *
     * Delete a Role
     *
     */

    @Transactional
    public void delete(String name){
        RoleEntity entity=repository.findByName(name)
                .orElseThrow(()-> new RoleNotFoundException(name));
        repository.delete(entity);
    }
}
