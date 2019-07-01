package com.fightnet.controllers.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class InvitesDTO {
    private UUID id;
    private BookedUser fighterInviter;
    private BookedUser fighterInvited;
    private float latitude;
    private float longitude;
    private String fightStyle;
    private String comment;
    private Date date;
    private boolean accepted;
}
