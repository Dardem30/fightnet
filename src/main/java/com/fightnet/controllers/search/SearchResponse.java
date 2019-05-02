package com.fightnet.controllers.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse<T> {
    private List<T> records;
    private long count;
}
