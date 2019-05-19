package com.fightnet.security.mail;

import com.sendgrid.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailService {


    public void sendCodeMessage(final String to, final String subject, final String text) throws Exception {
        final SendGrid sg = new SendGrid("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");
        final Request request = new Request();
        request.method = Method.POST;
        request.endpoint = "mail/send";
        request.body = new Mail(new Email("fightseeker0@gmail.com"), subject, new Email(to), new Content("text/plain", text)).build();
        sg.api(request);
    }
}