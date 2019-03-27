package com.fightnet.controllers.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class InvitesDTO {
    private ObjectId id;
    private UserDTO fighterInviter;
    private UserDTO fighterInvited;
    private float latitude;
    private float longitude;
    private String fightStyle;
    private Date date;
    private boolean accepted;
}
