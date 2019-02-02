package com.fightnet.dataAccess;

import com.fightnet.models.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDAO extends CrudRepository<Role, Long> {
    Role findByName(String name);
}