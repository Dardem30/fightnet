package com.fightnet.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
@Data
public class Notification {
    private String text;
    private String email;
    private float latitude;
    private float longitude;
}
