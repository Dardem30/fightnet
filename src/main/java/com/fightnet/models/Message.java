package com.fightnet.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("messages")
@Data
public class Message {
    private String userSender;
    private String userResiver;
    private String text;
    private Date date;
}
