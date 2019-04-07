package com.fightnet.controllers;

import com.fightnet.models.Message;
import com.fightnet.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/message")
@CrossOrigin("*")
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message useSimpleRest(@RequestBody final Message message) {
        messageService.saveMessage(message);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + message.getUserResiver(), message);
        this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + message.getUserSender(), message);
        return message;
    }

    @GetMapping("/getDialog")
    public List<Message> getDialog(@RequestParam("email1") final String email1, @RequestParam("email2") final String email2) {
        return messageService.getDialog(email1, email2);
    }
}