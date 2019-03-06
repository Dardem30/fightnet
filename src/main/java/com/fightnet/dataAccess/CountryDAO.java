package com.fightnet.dataAccess;

import com.fightnet.models.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryDAO extends CrudRepository<Country, Long> {
    List<Country> findAllByOrderByName();
}
