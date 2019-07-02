package com.fightnet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fightnet.FightnetApplication;
import com.fightnet.controllers.dto.BookedUser;
import com.fightnet.controllers.dto.InvitesDTO;
import com.fightnet.controllers.dto.UserDTO;
import com.fightnet.controllers.dto.VideoDTO;
import com.fightnet.controllers.search.MapSearchCriteria;
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
import java.util.Map;
import java.util.UUID;

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

    @GetMapping(value = "getCities")
    public List<City> getCities(@RequestParam("country") final String country) {
        return userService.getCities(country);
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

    @PostMapping(value = "getMarkers")
    public ResponseEntity<List<InvitesDTO>> getMarkers(@RequestBody final MapSearchCriteria searchCriteria) {
        return ResponseEntity.ok(userService.getMarkers(searchCriteria));
    }

    @PostMapping(value = "acceptInvite")
    public void updateInvite(@RequestBody final Invites invite) {
        userService.acceptInvite(invite);
        simpMessagingTemplate.convertAndSend("/socket-publisher/invite/" + invite.getFighterInviter().getEmail(), true);
    }
    @PostMapping(value = "declineInvite")
    public void declineInvite(@RequestParam("inviteId") final String inviteId) {
        simpMessagingTemplate.convertAndSend("/socket-publisher/invite/" + userService.declineInvite(UUID.fromString(inviteId)), true);
    }

    @PostMapping(value = "getNotifications")
    public List<Notification> getNotifications(@RequestBody final JsonNode email) {
        return userService.getNotifications(email.get("email").asText());
    }

    @PostMapping(value = "getPlannedFights")
    public SearchResponse<InvitesDTO> getPlannedFights(@RequestBody final JsonNode node) {
        return userService.getPlannedFights(node.get("email").asText(), node.get("page").asInt());
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
    @PostMapping(value = "getCommentsPhotos")
    public Map<String, String> getCommentsPhotos(@RequestBody final JsonNode emails) {
        return userService.getCommentsPhotos(emails.get("emails"));
    }
    @PostMapping(value = "updateChangableInfoToUser")
    public void updateChangableInfoToUser(@RequestBody final AppUser user) {
        userService.updateChangableInfoToUser(user);
    }
    @GetMapping(value = "getFacebookAccessToken")
    public ResponseEntity<String> getFacebookAccessToken() {
        return ResponseEntity.ok(FightnetApplication.facebookToken);
    }
}
