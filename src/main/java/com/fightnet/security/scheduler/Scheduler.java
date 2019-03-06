package com.fightnet.security.scheduler;

import com.fightnet.FightnetApplication;
import com.fightnet.dataAccess.UserDAO;
import com.fightnet.dataAccess.VideoDAO;
import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.TimeZone;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final UserDAO userRepository;
    private final VideoDAO videoRepository;

    @Scheduled(fixedDelay = 3600000)
    private void clearDatabase() {
        userRepository.findByRegistered(false).forEach(user -> {
            if (Calendar.getInstance(TimeZone.getTimeZone(user.getTimezone())).getTime().getTime() - user.getId().getDate().getTime() >= 3600000) {
                userRepository.delete(user);
            }
        });
    }

    @Scheduled(cron = "0 1 * * * ?")
    private void uploadVideoOnFacebook() {
        videoRepository.findTop10ByApprovedAndLoaded(true, false).forEach(video -> {
            try {
                final File file = new File(video.getUrl());
                final String videoId = uploadVideoToFacebook(file);
                if (file.delete()) {
                    log.info("File on url " + video.getUrl() + " is successfully deleted");
                } else {
                    log.error("FILE ON URL " + video.getUrl() + " WASN'T DELETED");
                }
                video.setUrl("https://www.facebook.com/100017201528846/videos/" + videoId);
                video.setLoaded(true);
                videoRepository.save(video);
            } catch (Exception e) {
                log.error("Failed to upload video: " + video.getUrl(), e);
            }
        });
        log.info("Scheduler finished uploading videos");
    }
    private String uploadVideoToFacebook(final File file) throws FileNotFoundException {
        final FacebookClient client = new DefaultFacebookClient(FightnetApplication.facebookToken, Version.VERSION_2_8);

        final FacebookType response = client.publish("359710211281042/videos", FacebookType.class,
                BinaryAttachment.with(file.getName(), new FileInputStream(file)));
        return response.getId();
    }
}