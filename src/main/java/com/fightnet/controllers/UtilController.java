package com.fightnet.controllers;

import com.fightnet.dataAccess.CountryDAO;
import com.fightnet.models.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "util/")
@RequiredArgsConstructor
public class UtilController {
    private final CountryDAO countryDAO;
    @GetMapping(value = "getCountries")
    public List<Country> getCountries() {
        return (List<Country>) countryDAO.findAll();
    }
}
