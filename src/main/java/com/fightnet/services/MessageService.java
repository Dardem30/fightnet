package com.fightnet.services;

import com.fightnet.models.AppUser;
import com.fightnet.models.Comment;
import com.fightnet.models.Message;
import com.fightnet.models.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MongoOperations operations;

    public void saveMessage(final Message message) {
        message.setDate(new Date());
        operations.save(message);
    }

    public List<Message> getDialog(final String email1, final String email2) {
        final Criteria criteria = new Criteria();
        criteria.and("userSender").in(email1, email2);
        criteria.and("userResiver").in(email1, email2);
        return operations.find(Query.query(criteria).with(new Sort(Sort.Direction.ASC, "date")), Message.class);
    }

    public void saveComment(final Comment comment) {
        final Video video = operations.findById(comment.getVideo().getUrl(), Video.class);
        final List<Comment> comments = video.getComments() == null ? new ArrayList<>() : video.getComments();
        comment.setDate(new Date());
        comments.add(comment);
        video.setComments(comments);
        operations.save(comment);
        operations.save(video);
    }

    public List<Message> getConversations(final String email) {
        final Map<String, Message> latestMassagesMap = new HashMap<>();
        for (final Message message: operations.find(Query.query(new Criteria().orOperator(Criteria.where("userSender").is(email), Criteria.where("userResiver").is(email))), Message.class)) {
            final String user = message.getUserResiver().equals(email) ? message.getUserSender() : message.getUserResiver();
            final Message lastMessage = latestMassagesMap.get(user);
            if (lastMessage == null) {
                latestMassagesMap.put(user, message);
            } else if (lastMessage.getDate().compareTo(message.getDate()) < 0){
                latestMassagesMap.put(user, message);
            }
        }
        return latestMassagesMap.entrySet().stream()
                .map(entry -> {
                    final AppUser user = operations.findById(entry.getKey(), AppUser.class);
                    return entry.getValue().setTitleName(user.getName() + " " + user.getSurname());
                })
                .collect(Collectors.toList());
    }
}
