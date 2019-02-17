package com.fightnet.controllers;

import com.fightnet.controllers.dto.UserDTO;
import com.fightnet.models.AppUser;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "user/")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final ModelMapper mapper;

    @GetMapping(value = "findUser")
    public ResponseEntity findUserByEmail(@RequestParam("email") final String email) {
        final AppUser sourceUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(mapper.createTypeMap(sourceUser, UserDTO.class)
                .addMapping(user -> user.getCountry().getName(), UserDTO::setCountry)
                .addMapping(user -> user.getCity().getName(), UserDTO::setCity)
                .map(sourceUser));
    }
}
