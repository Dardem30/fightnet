package com.fightnet.controllers.dto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UserDTO {
    private ObjectId id;
    private String name;
    private String surname;
    private String email;
    private String timezone;
    private String country;
    private String city;
    private String description;

    public UserDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public UserDTO setCity(String city) {
        this.city = city;
        return this;
    }
}
