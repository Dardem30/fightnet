package com.fightnet.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cities")
@Data
public class City {
    @Id
    private String name;
    private String country;
}
