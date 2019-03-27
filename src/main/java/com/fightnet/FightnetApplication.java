package com.fightnet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Properties;

@SpringBootApplication
public class FightnetApplication {
    public static String facebookToken = "EAAFdvUOtnA8BABqlHHwxVIJuDg4qio4J1tUu5Iqd3Ua8s3gYkJ3p02OsEQNDIMQNIstdsZBZBwlFVZC0ghi5NZCfMgYPftwHF4AnzRmhahQeSgyH3ZAODRJ5rlTeiZBaWzcFkTimE0V4WnN8wPMRMNKU27A7wpUZCnlG00zHRZCOGlUFz2E9nobfwqUpOeTLjiAqhkHZCaIhZBZArDvGuxyjNsMDgt4S9dUtOY8uelQkk2H5gZDZD";
    @Value("${spring.mail.username}")
    private String mailName;
    @Value("${spring.mail.password}")
    private String mailPassword;

    public static void main(String[] args) {
        SpringApplication.run(FightnetApplication.class, args);
    }

    @Bean(name = "passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setUsername(mailName);
        mailSender.setPassword(mailPassword);

        final Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

