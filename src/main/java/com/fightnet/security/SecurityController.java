package com.fightnet.security;


import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.fightnet.security.SecurityConstants.HEADER;
import static com.fightnet.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class SecurityController {
    private final UserService userService;

    @PostMapping("/sendCode")
    public String sendCode(@RequestBody final AppUser user) {
        try {
            return userService.sendCode(user);
        } catch (Exception e) {
            log.info("Sorry but user with this email already exists", e);
            return "Sorry but user with this email already exists";
        }
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestParam("email") final String email, @RequestParam("code") final String code) {
        try {
            return userService.saveUser(email, code);
        } catch (Exception e) {
            log.info("Wrong code", e);
            return "Wrong code";
        }
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