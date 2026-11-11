package org.acme.bot.application.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.acme.bot.application.dto.RoleDTO;
import org.acme.bot.application.dto.usecases.CreateRoleRequest;
import org.acme.bot.infrastructure.mapper.RoleMapper;
import org.acme.bot.infrastructure.persistence.entity.RoleEntity;
import org.acme.bot.infrastructure.persistence.repository.RoleRepository;

import java.util.List;
import java.util.NoSuchElementException;

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
     * Create a Role using business logic
     *
     */

    @Transactional
    public RoleDTO create(CreateRoleRequest request){
        //Step 1 : Mapping (Validation happens inside Mapper/Domain Record)
        RoleEntity entity=RoleMapper.toEntity(request);

        //Step 2 : Integrity Rule (Using Standard Java Exceptions)
        if(repository.findByName(entity.getName()).isPresent()){
            throw new EntityExistsException("Role already exists");
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
                .orElseThrow(()-> new NoSuchElementException("Product not found: "+name));
        repository.delete(entity);
    }
}
