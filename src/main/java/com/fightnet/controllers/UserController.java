package com.fightnet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
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
    public ResponseEntity<String> uploadVideo(@RequestBody final JsonNode request) {

        try {
            if (request.get("inviteId") != null) {
                userService.deleteInvitation(UUID.fromString(request.get("inviteId").asText()));
            }
            userService.saveVideo((MultipartFile) request.get("file"), request.get("fighterEmail1").asText(), request.get("fighterEmail2").asText(), request.get("style").asText());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during trying to send video on review", e);
            throw new RuntimeException();
        }
    }
    @PostMapping(value = "uploadPhoto")
    public ResponseEntity<String> uploadPhoto(@RequestBody final JsonNode request) {
        try {
            userService.savePhoto((MultipartFile) request.get("file"), request.get("email").asText());
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
