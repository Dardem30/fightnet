package com.fightnet.controllers;

import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "admin/")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
    private final UserService userService;
    @GetMapping(value = "{username}")
    public AppUser getUserByUsername(@PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }
}
