package com.fightnet.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookedPersons")
@Data
public class BookedPersons {
    private String user1;
    private String user2;
}
