package com.fightnet.dataAccess;

import com.fightnet.models.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryDAO extends CrudRepository<Country, Long> {
}
