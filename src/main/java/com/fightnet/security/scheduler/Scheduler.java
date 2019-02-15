package com.fightnet.security.scheduler;

import com.fightnet.dataAccess.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final UserDAO userRepository;

    @Scheduled(fixedDelay = 3600000)
    private void clearDatabase() {
        userRepository.findByRegistered(false).forEach(user -> {
            if (Calendar.getInstance(TimeZone.getTimeZone(user.getTimezone())).getTime().getTime() - user.getId().getDate().getTime() >= 3600000) {
                userRepository.delete(user);
            }
        });
    }
}