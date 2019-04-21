package com.fightnet.controllers.dto;

import com.fightnet.models.Comment;
import lombok.Data;

import java.util.Set;

@Data
public class VideoDTO {
    private String url;
    private BookedUser fighter1;
    private BookedUser fighter2;
    private Set<BookedUser> votes1;
    private Set<BookedUser> votes2;
    private Set<Comment> comments;
    private String style;
}
