package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "countries")
@Data
public class Country {
    private ObjectId id;
    private String name;
    @DBRef
    private Set<City> cities;
}
