package com.fightnet.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "countries")
@Data
public class Country {
    @Id
    private String name;
}
