package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "notification")
@Data
public class Notification {
    @Id
    private ObjectId id;
    private String text;
    private String email;
    private float latitude;
    private float longitude;
    private Date createTime;
    private boolean readed;
}
