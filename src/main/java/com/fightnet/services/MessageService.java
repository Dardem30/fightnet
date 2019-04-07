package com.fightnet.services;

import com.fightnet.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
}
