package com.fightnet.controllers;

import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "user/")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping(value = "uploadVideo")
    public String uploadVideo(@RequestParam("file") final MultipartFile file, @RequestParam("fighterEmail1") final String email1, @RequestParam("fighterEmail2") final String email2) {
        try {
            userService.saveVideo(file, email1, email2);
            return "success";
        } catch (Exception e) {
            log.error("Exception during trying to upload video", e);
            throw new RuntimeException();
        }
    }
}
