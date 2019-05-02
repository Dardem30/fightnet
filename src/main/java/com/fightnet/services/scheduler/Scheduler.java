package com.fightnet.services.scheduler;

import com.fightnet.models.AppUser;
import com.fightnet.models.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class Scheduler {
    private final MongoOperations operations;

    @Scheduled(cron = "0 0 0 1/5 * ?")
    public void countVideos() throws ParseException {
        final Date aWeekAgo = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().minusWeeks(1).toString());
        final List<Video> videos = operations.find(Query.query(new Criteria().and("voteStarts").lt(aWeekAgo)), Video.class);
        for (final Video video : videos) {
            final AppUser user1 = operations.findById(video.getFighter1().getEmail(), AppUser.class);
            final AppUser user2 = operations.findById(video.getFighter2().getEmail(), AppUser.class);
            final int votesForUser1 = video.getVotes1() == null ? 0 : video.getVotes1().size();
            final int votesForUser2 = video.getVotes2() == null ? 0 : video.getVotes2().size();
            if (votesForUser1 > votesForUser2) {
                final Map<String, Integer> user1WinsMap = user1.getWins() == null ? new HashMap<>() : user1.getWins();
                final Map<String, Integer> user2LosesMap = user2.getLoses() == null ? new HashMap<>() : user2.getLoses();
                final int user1Wins = user1WinsMap.get(video.getStyle()) == null ? 0 : user1WinsMap.get(video.getStyle());
                final int user2Loses = user2LosesMap.get(video.getStyle()) == null ? 0 : user2LosesMap.get(video.getStyle());
                user1WinsMap.put(video.getStyle(), user1Wins + 1);
                user2LosesMap.put(video.getStyle(), user2Loses + 1);
                user1.setWins(user1WinsMap);
                user2.setLoses(user2LosesMap);
                operations.save(user1);
                operations.save(user2);
                operations.remove(video);
            } else if (votesForUser1 < votesForUser2) {
                final Map<String, Integer> user2WinsMap = user2.getWins() == null ? new HashMap<>() : user2.getWins();
                final Map<String, Integer> user1LosesMap = user1.getLoses() == null ? new HashMap<>() : user1.getLoses();
                final int user2Wins = user2WinsMap.get(video.getStyle()) == null ? 0 : user2WinsMap.get(video.getStyle());
                final int user1Loses = user1LosesMap.get(video.getStyle()) == null ? 0 : user1LosesMap.get(video.getStyle());
                user1LosesMap.put(video.getStyle(), user1Loses + 1);
                user2WinsMap.put(video.getStyle(), user2Wins + 1);
                user1.setLoses(user1LosesMap);
                user2.setWins(user2WinsMap);
                operations.save(user1);
                operations.save(user2);
                operations.remove(video);
            }

        }
        log.info("Scheduler successfully finished counting wins/loses");
    }
}
