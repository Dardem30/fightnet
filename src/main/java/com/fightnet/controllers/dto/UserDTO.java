package com.fightnet.controllers.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String name;
    private String surname;
    private String email;
    private String timezone;
    private String country;
    private String city;
    private String description;
    private List<BookedUser> bookedUsers;

    public UserDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public UserDTO setCity(String city) {
        this.city = city;
        return this;
    }
    public UserDTO setBookedUsers(List<BookedUser> bookedUsers) {
        this.bookedUsers = bookedUsers;
        return this;
    }
}
