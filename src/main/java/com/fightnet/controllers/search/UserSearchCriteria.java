package com.fightnet.controllers.search;

import lombok.Data;

@Data
public class UserSearchCriteria {
    private String name = "";
    private String surname = "";
    private String description = "";
}
