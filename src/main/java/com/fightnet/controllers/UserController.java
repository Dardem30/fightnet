package com.fightnet.controllers;

import com.fightnet.models.Invites;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "user/")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping(value = "uploadVideo")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") final MultipartFile file,
                                              @RequestParam("fighterEmail1") final String email1,
                                              @RequestParam("fighterEmail2") final String email2,
                                              @RequestParam("style") final String style,
                                              @RequestParam(value = "inviteId", required = false) final UUID inviteId) {
        if (inviteId != null) {
            userService.deleteInvitation(inviteId);
        }
        userService.saveVideo(file, email1, email2, style);
        return ResponseEntity.ok("success");
    }

    @PostMapping(value = "invite")
    public ResponseEntity<Invites> invite(@RequestBody final Invites invite) {
        try {
            userService.createUpdateInvitation(invite);
            return ResponseEntity.ok(invite);
        } catch (Exception e) {
            log.error("Error during trying to make invitation", e);
            throw new RuntimeException(e);
        }
    }
}
