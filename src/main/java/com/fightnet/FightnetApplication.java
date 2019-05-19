package com.fightnet;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FightnetApplication {
    public static String facebookToken = "EAAFdvUOtnA8BAJEU1ZCjTRhtB9CAzYQFa3GO8AwQidpSUZCAGZAOTe2zJIyeCuEK3uZCMIpXLi55NqtvpkZCOi6DZBvu5mptZAuyQ4PhT9hbievaX2XayoPXgIMeqWItATm09WKLDnZCUywGyEQaJN4TwpXHfie09W4F4AUtIG1M92Ho67UZAR4p7swItsrYan3Xj5gzhJ89gUwZDZD";

    public static void main(String[] args) {
        SpringApplication.run(FightnetApplication.class, args);
    }

    @Bean(name = "passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

