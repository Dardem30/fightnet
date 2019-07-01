package com.fightnet.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document(collection = "invites")
@Data
@NoArgsConstructor
public class Invites {
    @Id
    private UUID id;
    private AppUser fighterInviter;
    private AppUser fighterInvited;
    private float latitude;
    private float longitude;
    private String fightStyle;
    private String comment;
    private Date date;
    private boolean accepted;

    public Invites(UUID id) {
        this.id = id;
    }
}
