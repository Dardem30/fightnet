package com.fightnet.dataAccess;

import com.fightnet.models.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDAO extends CrudRepository<AppUser, String> {
    AppUser findByEmail(String email);
    List<AppUser> findByRegistered(boolean registered);
}