package com.fightnet.controllers.search;

import lombok.Data;

@Data
public class VideoSearchCriteria {
    private String name;
    private String style;
    private int pageNum;
}
