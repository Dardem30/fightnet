package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Data
@Document("comment")
public class Comment {
    @Id
    private ObjectId id;
    private String text;
    private String userFullName;
    private String email;
    private Date date;
    @Transient
    private Set<String> emails;
    @Transient
    private Video video;
}
