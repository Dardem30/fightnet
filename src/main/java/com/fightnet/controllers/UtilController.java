package com.fightnet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fightnet.controllers.dto.BookedUser;
import com.fightnet.controllers.dto.InvitesDTO;
import com.fightnet.controllers.dto.UserDTO;
import com.fightnet.controllers.dto.VideoDTO;
import com.fightnet.controllers.search.SearchResponse;
import com.fightnet.controllers.search.UserSearchCriteria;
import com.fightnet.controllers.search.VideoSearchCriteria;
import com.fightnet.models.*;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "util/")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UtilController {
    private final UserService userService;
    private final ModelMapper mapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping(value = "getCountries")
    public List<Country> getCountries() {
        return userService.findAllCountries();
    }

    @PostMapping(value = "findUser")
    public ResponseEntity findUserByEmail(@RequestBody final JsonNode email) {
        return ResponseEntity.ok(mapper.map(userService.findUserByEmail(email.get("email").asText()), UserDTO.class));
    }

    @PostMapping(value = "listUsers")
    public ResponseEntity<SearchResponse<AppUser>> listUsers(@RequestBody final UserSearchCriteria searchCriteria) {
        return ResponseEntity.ok(userService.list(searchCriteria));
    }

    @PostMapping(value = "bookPerson")
    public void bookPerson(@RequestBody final JsonNode request) {
        userService.bookPerson(request.get("currentUserEmail").asText(), request.get("personEmail").asText());
    }

    @PostMapping(value = "unBookPerson")
    public void unBookPerson(@RequestBody final JsonNode request) {
        userService.unBookPerson(request.get("currentUserEmail").asText(), request.get("email").asText());
    }

    @PostMapping(value = "getBookedPersons")
    public ResponseEntity<List<BookedUser>> getBookedPersons(@RequestBody final JsonNode currentUserEmail) {
        return ResponseEntity.ok(userService.getBookedPersons(currentUserEmail.get("currentUserEmail").asText()));
    }

    @PostMapping(value = "getInvitesForUser")
    public ResponseEntity<SearchResponse<InvitesDTO>> getInvitesForUser(@RequestBody final JsonNode request) {
        return ResponseEntity.ok(userService.getInvitesForUser(request.get("email").asText(), request.get("page").asInt()));
    }

    @GetMapping(value = "getMarkers")
    public ResponseEntity<List<InvitesDTO>> getMarkers() {
        return ResponseEntity.ok(userService.getMarkers());
    }

    @PostMapping(value = "acceptInvite")
    public void updateInvite(@RequestBody final Invites invite) {
        userService.acceptInvite(invite);
        simpMessagingTemplate.convertAndSend("/socket-publisher/invite/" + invite.getFighterInviter().getEmail(), true);
    }

    @PostMapping(value = "getNotifications")
    public List<Notification> getNotifications(@RequestBody final JsonNode email) {
        return userService.getNotifications(email.get("email").asText());
    }

    @PostMapping(value = "getPlannedFights")
    public List<InvitesDTO> getPlannedFights(@RequestBody final JsonNode email) {
        return userService.getPlannedFights(email.get("email").asText());
    }

    @PostMapping(value = "getVideos")
    public SearchResponse<VideoDTO> getVideos(@RequestBody() final VideoSearchCriteria searchCriteria) {
        return userService.getVideos(searchCriteria);
    }

    @PostMapping(value = "vote")
    public void vote(@RequestBody final Video video) {
        userService.vote(video);
    }

    @PostMapping(value = "resetNotifications")
    public void resetNotifications(@RequestBody final JsonNode email) {
        userService.resetNotifications(email.get("email").asText());
    }
    @PostMapping(value = "resetMessages")
    public void resetMessages(@RequestBody final JsonNode email) {
        userService.resetMessages(email.get("email").asText());
    }
}
