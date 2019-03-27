package com.fightnet.controllers.search;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UserSearchCriteria {
    private String name;
    private String surname;
    private String description;
    private ObjectId country;
    private ObjectId city;
    private String searcherEmail;
}
