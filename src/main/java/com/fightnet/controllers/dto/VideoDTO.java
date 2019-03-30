package com.fightnet.controllers.dto;

import lombok.Data;

@Data
public class VideoDTO {
    private String url;
    private BookedUser fighter1;
    private BookedUser fighter2;
    private Integer votes1;
    private Integer votes2;
}
