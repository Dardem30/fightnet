package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cities")
@Data
public class City {
    private ObjectId id;
    private String name;
}
