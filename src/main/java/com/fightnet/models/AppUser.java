package com.fightnet.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Document(collection = "users")
@Data
public class AppUser implements UserDetails{
    @Id
    private String email;
    private boolean registered;
    private String password;
    private String name;
    private String surname;
    private String code;
    private String timezone;
    @DBRef
    private Set<Role> roles;
    @DBRef
    private Set<Video> videos;
    @DBRef
    private Country country;
    @DBRef
    private City city;
    private String description;
    @Transient
    private List<AppUser> bookedPeople;
    private Date createTime;
    private Map<String, Integer> wins;
    private Map<String, Integer> loses;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
