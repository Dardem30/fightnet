package com.fightnet.controllers.dto;

import com.fightnet.models.City;
import com.fightnet.models.Country;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class UserDTO {
    private ObjectId id;
    private String name;
    private String surname;
    private String email;
    private String timezone;
    @DBRef
    private Country country;
    @DBRef
    private City city;
    private String description;
}
