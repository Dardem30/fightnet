package com.fightnet.controllers.search;

import lombok.Data;

@Data
public class UserSearchCriteria {
    private String name;
    private String surname;
    private String description;
    private String country;
    private String city;
    private String searcherEmail;
    private int pageNum;
    private String preferredKind;
    private String width;
    private String height;
}
