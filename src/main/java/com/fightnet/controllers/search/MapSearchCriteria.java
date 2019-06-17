package com.fightnet.controllers.search;

import lombok.Data;

import java.util.Date;

@Data
public class MapSearchCriteria {
    private String name;
    private String fightStyle;
    private Date startDate;
    private Date endDate;
}
