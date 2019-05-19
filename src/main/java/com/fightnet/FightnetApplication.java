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
    public static String facebookToken = "EAAFdvUOtnA8BAJEU1ZCjTRhtB9CAzYQFa3GO8AwQidpSUZCAGZAOTe2zJIyeCuEK3uZCMIpXLi55NqtvpkZCOi6DZBvu5mptZAuyQ4PhT9hbievaX2XayoPXgIMeqWItATm09WKLDnZCUywGyEQaJN4TwpXHfie09W4F4AUtIG1M92Ho67UZAR4p7swItsrYan3Xj5gzhJ89gUwZDZD";
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

        System.out.println(mailSender);
        System.out.println(props);
        System.out.println(mailName);
        System.out.println(mailPassword);
        return mailSender;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

