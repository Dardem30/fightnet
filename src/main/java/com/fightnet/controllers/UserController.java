package com.fightnet.controllers;

import com.fightnet.models.Invites;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping(value = "uploadVideo")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") final MultipartFile file,
                                              @RequestParam("fighterEmail1") final String email1,
                                              @RequestParam("fighterEmail2") final String email2,
                                              @RequestParam("style") final String style,
                                              @RequestParam(value = "inviteId", required = false) final UUID inviteId) {

        try {
            if (inviteId != null) {
                userService.deleteInvitation(inviteId);
            }
            userService.saveVideo(file, email1, email2, style);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during trying to send video on review", e);
            throw new RuntimeException();
        }
    }
    @PostMapping(value = "uploadPhoto")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") final MultipartFile file, @RequestParam("email") final String email) {
        try {
            userService.savePhoto(file, email);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during trying to send video on review", e);
            throw new RuntimeException();
        }
    }

    @PostMapping(value = "invite")
    public ResponseEntity<Invites> invite(@RequestBody final Invites invite) {
        try {
            userService.invite(invite);
            simpMessagingTemplate.convertAndSend("/socket-publisher/invite/" + invite.getFighterInvited().getEmail(), true);
            return ResponseEntity.ok(invite);
        } catch (Exception e) {
            log.error("Error during trying to make invitation", e);
            throw new RuntimeException(e);
        }
    }
}
