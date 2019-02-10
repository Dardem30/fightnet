package com.fightnet;

import com.fightnet.dataAccess.CityDAO;
import com.fightnet.dataAccess.CountryDAO;
import com.fightnet.models.City;
import com.fightnet.models.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class FightnetApplication {
	private final CountryDAO countryDAO;
	private final CityDAO cityDAO;

	public static void main(String[] args) {
		SpringApplication.run(FightnetApplication.class, args);
	}

	@Bean(name = "passwordEncoder")
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		this.fillDatabase();
		return new BCryptPasswordEncoder();
	}
	private void fillDatabase() {
		final Country country = new Country();
		country.setName("Беларусь");
		final Set<City> cities = new HashSet<>();
		try(final BufferedReader reader = new BufferedReader(new FileReader("CitiesBY.txt"))) {
			String line = reader.readLine();
			while (line != null) {
				City city = new City();
				city.setName(line);
				cityDAO.save(city);
				cities.add(city);
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		country.setCities(cities);
		countryDAO.save(country);
	}
}

