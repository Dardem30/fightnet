package com.fightnet.services;

import com.fightnet.models.Comment;
import com.fightnet.models.Message;
import com.fightnet.models.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final Set<Comment> comments = video.getComments() == null ? new HashSet<>() : video.getComments();
        comment.setDate(new Date());
        comments.add(comment);
        video.setComments(comments);
        operations.save(comment);
        operations.save(video);
    }
}
