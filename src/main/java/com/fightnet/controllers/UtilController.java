package com.fightnet.controllers;

import com.fightnet.controllers.dto.UserDTO;
import com.fightnet.controllers.search.UserSearchCriteria;
import com.fightnet.dataAccess.CountryDAO;
import com.fightnet.models.AppUser;
import com.fightnet.models.City;
import com.fightnet.models.Country;
import com.fightnet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "util/")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UtilController {
    private final CountryDAO countryDAO;
    private final UserService userService;
    private final ModelMapper mapper;

    @GetMapping(value = "getCountries")
    public List<Country> getCountries() {
        return countryDAO.findAllByOrderByName().stream()
                .peek(country -> country.setCities(country.getCities().stream().sorted(Comparator.comparing(City::getName)).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "findUser")
    public ResponseEntity findUserByEmail(@RequestParam("email") final String email) {
        final AppUser user = userService.findUserByEmail(email);
        return ResponseEntity.ok(mapper.map(user, UserDTO.class)
                .setCountry(user.getCountry().getName())
                .setCity(user.getCity().getName()));
    }

    @PostMapping(value = "listUsers")
    public ResponseEntity<List<UserDTO>> listUsers(@RequestBody final UserSearchCriteria searchCriteria) {
        final List<UserDTO> result = userService.list(searchCriteria).stream()
                .map(user -> mapper.map(user, UserDTO.class)
                        .setCountry(user.getCountry().getName())
                        .setCity(user.getCity().getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
