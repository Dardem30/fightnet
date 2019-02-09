package com.fightnet.security;


import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.fightnet.security.SecurityConstants.HEADER;
import static com.fightnet.security.SecurityConstants.TOKEN_PREFIX;

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
    @PostMapping("/login")
    public String login(HttpServletResponse response, @RequestBody final AppUser user) {
        final String token = userService.authenticate(user);
        if (token != null) {
            response.addHeader(HEADER, TOKEN_PREFIX + token);
            return "Success";
        }
        return "Wrong username or password";
    }
}