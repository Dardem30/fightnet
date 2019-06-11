package com.fightnet.controllers.forms;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoForm {
    private MultipartFile file;
    private String fighterEmail1;
    private String fighterEmail2;
    private String inviteId;
    private String style;
}
