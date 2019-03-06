package com.fightnet.dataAccess;

import com.fightnet.models.AppUser;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDAO extends CrudRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    List<AppUser> findByRegistered(boolean registered);
    @Query(value = "{'name': {$regex : ?0, $options: 'i'}, 'surname': {$regex : ?1, $options: 'i'}, 'description': {$regex : ?2, $options: 'i'}}")
    List<AppUser> findByNameLikeAndSurnameLikeAndDescriptionLike(String name, String surname, String description);
}