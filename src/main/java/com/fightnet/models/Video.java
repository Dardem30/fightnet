package com.fightnet.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
@Data
public class Video {
    private ObjectId id;
    private String url;
    private AppUser fighter1;
    private AppUser fighter2;
    private Integer votes1;
    private Integer votes2;
    private boolean approved;
    private boolean loaded;// on facebook
}
