package com.fightnet;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FightnetApplication {
    public static String facebookToken = "EAAFdvUOtnA8BAOqZA9NSRE97QJEDvj5SQVNIp1PMIr8oZBYqqh0BzsPtx5JEyIX8gcZB7lK0haHWpz9ktdUQghZBzdWZCjmGZCcDZBJYZCXNj0aoV2ei0cx2OV3NNT4ywLqJCNtZA8sMiI6fHWbaznjbAmMxwPZAear9Uu3idrP53yp5ZBZBi6KZA9RONAB90zdoERKZACH8BBlWrWswZDZD";

    public static void main(String[] args) {
        SpringApplication.run(FightnetApplication.class, args);
    }

    @Bean(name = "passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
//        try {
//            final Gson gson = new Gson();
//            final LinkedTreeMap<String, ArrayList<String>> map = gson.fromJson(new FileReader("countries.json"), LinkedTreeMap.class);
//            for (final Map.Entry<String, ArrayList<String>> entry: map.entrySet()) {
//                final Country country = new Country();
//                country.setName(entry.getKey());
//                operations.save(country);
//                for (final String cityName: entry.getValue()) {
//                    final City city = new City();
//                    city.setName(cityName);
//                    city.setCountry(country.getName());
//                    operations.save(city);
//                }
//                System.out.println("New country: " + country.getName());;
//            }
//            final Country country = new Country();
//            country.setName("United States");
//            operations.save(country);
//            System.out.println("New country: " + country.getName());;
//            final List<LinkedTreeMap<String, String>> list = gson.fromJson(new FileReader("citiesRU.json"), List.class);
//            for (LinkedTreeMap<String, String> usmap: list) {
//                for (Map.Entry<String, String> entry : usmap.entrySet()) {
//                    if (entry.getKey().equals("city")) {
//                        final City city = new City();
//                        city.setName(entry.getValue());
//                        city.setCountry("United States");
//                        operations.save(city);
//                    }
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Countries are loaded");
        return new ModelMapper();
    }
}

