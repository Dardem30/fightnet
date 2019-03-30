package com.fightnet.controllers;

import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "admin/")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class AdminController {
    private final UserService userService;

    @GetMapping(value = "{email}")
    public AppUser getUserByUsername(@PathVariable("email") String email) {
        return userService.findUserByEmail(email);
    }

    @PostMapping(value = "uploadVideoToFacebook")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") final MultipartFile file) {
        try {
            userService.saveVideoToFacebook(file);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during trying to upload video to facebook");
            throw new RuntimeException(e);
        }
    }
}
