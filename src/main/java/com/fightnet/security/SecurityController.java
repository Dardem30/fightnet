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

    @PostMapping("/sendCode")
    public String sendCode(@RequestBody final AppUser user) {
        return userService.sendCode(user);
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestParam("username") final String username, @RequestParam("code") final String code) {
        return userService.saveUser(username, code);
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