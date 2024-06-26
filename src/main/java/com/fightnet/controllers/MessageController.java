package com.fightnet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fightnet.models.Comment;
import com.fightnet.models.Message;
import com.fightnet.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/message")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @PostMapping("/send")
    public Message send(@RequestBody final Message message) {
        messageService.saveMessage(message);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + message.getUserResiver(), message);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + message.getUserSender(), message);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/messages/" + message.getUserResiver(), true);
        return message;
    }
    @PostMapping("/sendComment")
    public Comment sendComment(@RequestBody final Comment comment) {
        messageService.saveComment(comment);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + comment.getVideo().getUrl(), comment);
        return comment;
    }

    @PostMapping("/getDialog")
    public List<Message> getDialog(@RequestBody final JsonNode request) {
        return messageService.getDialog(request.get("email1").asText(), request.get("email2").asText());
    }
    @PostMapping("/getConversations")
    public List<Message> getConversations(@RequestBody final JsonNode email) {
        return messageService.getConversations(email.get("email").asText());
    }
}