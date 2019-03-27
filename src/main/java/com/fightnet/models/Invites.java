package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "invites")
@Data
public class Invites {
    @Id
    private ObjectId id;
    @DBRef
    private AppUser fighterInviter;
    @DBRef
    private AppUser fighterInvited;
    private float latitude;
    private float longitude;
    private String fightStyle;
    private Date date;
    private boolean accepted;
}
