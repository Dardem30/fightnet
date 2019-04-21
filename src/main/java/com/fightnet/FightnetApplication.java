package com.fightnet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Properties;

@SpringBootApplication
public class FightnetApplication {
    public static String facebookToken = "EAAFdvUOtnA8BAI3RTZBKd2ecZCiIh8t0VbQjeGZC3L2VFJqFQbV7BSi9f2sEMuBr8qqWAviPGB2ZBUAp71hFvIYtuGVB5HAjzrLjNXi5XI7PcNtzrikvcrd9DapZCYM3tABt2iVhGvVFZBM5mOnUbMDwooURuCD1JEGOwMTDKYPR9imTW8cnrVi6LRBZC451j9VueHZBZC3pMwgZDZD";
    @Value("${spring.mail.username}")
    private String mailName;
    @Value("${spring.mail.password}")
    private String mailPassword;
    @Autowired
    private MongoOperations operations;

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

