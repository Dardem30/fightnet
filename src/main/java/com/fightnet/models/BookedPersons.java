package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookedPersons")
@Data
public class BookedPersons {
    @Id
    private ObjectId id;
    private String user1;
    private String user2;
}
