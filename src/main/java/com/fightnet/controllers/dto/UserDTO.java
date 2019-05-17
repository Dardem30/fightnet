package com.fightnet.controllers.dto;

import com.fightnet.models.Role;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Set<Role> roles;
    private Map<String, Integer> wins;
    private Map<String, Integer> loses;
    private Set<String> photos;
    private String mainPhoto;
    private Integer notifications;
    private Integer unreadedMessages;

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
