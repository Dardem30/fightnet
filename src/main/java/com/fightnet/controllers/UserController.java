package com.fightnet.controllers;

import com.fightnet.controllers.dto.UserDTO;
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

    @GetMapping(value = "findUser/{email}")
    public ResponseEntity<UserDTO> findUserByEmail(@PathVariable("email") final String email) {
        return ResponseEntity.ok(mapper.map(userService.findUserByEmail(email), UserDTO.class));
    }
}
