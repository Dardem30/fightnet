package com.fightnet.controllers.forms;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageForm {
    private MultipartFile file;
    private String email;
}
