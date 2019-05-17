package com.fightnet.controllers;

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

    @GetMapping(value = "findUser")
    public ResponseEntity findUserByEmail(@RequestParam("email") final String email) {
        final AppUser user = userService.findUserByEmail(email);
        return ResponseEntity.ok(mapper.map(user, UserDTO.class));
    }

    @PostMapping(value = "listUsers")
    public ResponseEntity<SearchResponse<AppUser>> listUsers(@RequestBody final UserSearchCriteria searchCriteria) {
        return ResponseEntity.ok(userService.list(searchCriteria));
    }

    @GetMapping(value = "bookPerson")
    public void bookPerson(@RequestParam("currentUserEmail") final String currentUserEmail, @RequestParam("personEmail") final String personEmail) {
        userService.bookPerson(currentUserEmail, personEmail);
    }

    @GetMapping(value = "unBookPerson")
    public void unBookPerson(@RequestParam("currentUserEmail") final String currentUserEmail, @RequestParam("personEmail") final String personEmail) {
        userService.unBookPerson(currentUserEmail, personEmail);
    }

    @GetMapping(value = "getBookedPersons")
    public ResponseEntity<List<BookedUser>> getBookedPersons(@RequestParam("currentUserEmail") final String currentUserEmail) {
        return ResponseEntity.ok(userService.getBookedPersons(currentUserEmail));
    }

    @GetMapping(value = "getInvitesForUser")
    public ResponseEntity<SearchResponse<InvitesDTO>> getInvitesForUser(@RequestParam("email") final String email, @RequestParam("page") final Integer page) {
        return ResponseEntity.ok(userService.getInvitesForUser(email, page));
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

    @GetMapping(value = "getNotifications")
    public List<Notification> getNotifications(@RequestParam("email") final String email) {
        return userService.getNotifications(email);
    }

    @GetMapping(value = "getPlannedFights")
    public List<InvitesDTO> getPlannedFights(@RequestParam("email") final String email) {
        return userService.getPlannedFights(email);
    }

    @PostMapping(value = "getVideos")
    public SearchResponse<VideoDTO> getVideos(@RequestBody() final VideoSearchCriteria searchCriteria) {
        return userService.getVideos(searchCriteria);
    }

    @PostMapping(value = "vote")
    public void vote(@RequestBody final Video video) {
        userService.vote(video);
    }

    @GetMapping(value = "resetNotifications")
    public void resetNotifications(@RequestParam("email") final String email) {
        userService.resetNotifications(email);
    }
    @GetMapping(value = "resetMessages")
    public void resetMessages(@RequestParam("email") final String email) {
        userService.resetMessages(email);
    }
}
