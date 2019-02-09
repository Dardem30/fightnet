package com.fightnet.security;


import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
@CrossOrigin
public class SecurityController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public String signUp(@RequestBody final AppUser user) {
        return userService.saveUser(user);
    }
}