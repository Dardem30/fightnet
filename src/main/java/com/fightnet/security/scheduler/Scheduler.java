package com.fightnet.security.scheduler;

import com.fightnet.dataAccess.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {
    private final UserDAO userRepository;

    @Scheduled(fixedDelay = 3600000)
    private void clearDatabase() {
        userRepository.deleteByRegistered(false);
    }
}