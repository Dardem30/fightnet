package com.fightnet.models;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("messages")
@Data
public class Message {
    private String userSender;
    private String userResiver;
    private String text;
    private Date date;
    @Transient
    private String titleName;

    public String getTitleName() {
        return titleName;
    }

    public Message setTitleName(String titleName) {
        this.titleName = titleName;
        return this;
    }
}
