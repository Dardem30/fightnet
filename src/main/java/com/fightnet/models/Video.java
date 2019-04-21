package com.fightnet.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document(collection = "videos")
@Data
public class Video {
    @Id
    private String url;
    private AppUser fighter1;
    private AppUser fighter2;
    @DBRef
    private Set<AppUser> votes1;
    @DBRef
    private Set<AppUser> votes2;
    @DBRef
    private Set<Comment> comments;
    private Date voteStarts;
    private String style;
}
