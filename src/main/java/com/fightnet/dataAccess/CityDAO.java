package com.fightnet.dataAccess;

import com.fightnet.models.City;
import org.springframework.data.repository.CrudRepository;

public interface CityDAO extends CrudRepository<City, Long> {}
