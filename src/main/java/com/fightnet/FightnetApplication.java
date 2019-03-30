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
    public static String facebookToken = "EAAFdvUOtnA8BAFsLMBtZADdhhfbccZB1L1SvuEHWYLMS9Q1pJgn0NHZA5MfvbHBzrsQZBW9ejeK08CYFOCjXBu8YYgkIyYMN0otbD2xOu5D3s8VKEAvmkwZC0lMHfDANA1QLTx1ZCrrZC8LMGZCUjoujSliLlEroiMfCWcTvDYuLVtMYm0px9uqkCJVVmQXZBxpvmRr1wEfiXIeGljTjbPfWKJYk6hi4ccZAlPbaeWkqzHgAZDZD";
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

