package com.fightnet.controllers;

import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "admin/")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    @GetMapping(value = "{username}")
    public AppUser getUserByUsername(@PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }
}
