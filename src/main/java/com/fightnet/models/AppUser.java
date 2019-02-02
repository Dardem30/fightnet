package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
@Data
public class AppUser {
    private ObjectId id;
    private String username;
    private String password;
    @DBRef
    private Set<Role> roles;
}
