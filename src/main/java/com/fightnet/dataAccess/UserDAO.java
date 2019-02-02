package com.fightnet.dataAccess;

import com.fightnet.models.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}