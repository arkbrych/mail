package com.controller;

import com.dto.ActivationTemplateDto;
import com.dto.EmailDetails;
import com.openapi.api.MailApi;
import com.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/// Mail class for email testing

@RestController
@RequiredArgsConstructor
public class MailController implements MailApi {

    private final EmailService emailService;

    @Override
    public ResponseEntity<Boolean> testMail() {

        EmailDetails details = EmailDetails
                .builder()
                .subject("mail aktywacyjny")
                .recipient(List.of("name@cos.com"))
                .build();
        ActivationTemplateDto templateDto = ActivationTemplateDto
                .builder()
                .name("Name")
                .surname("Surname")
                .phoneNumber("12346567")
                .mail("deweloperto@heh.pl")
                .companyName("deweloper")
                .activationCode("https://www.wp.pl")
                .build();

        emailService.sendActivationEmail(details, templateDto);
//        emailService.sendFailActivationEmail(details, templateDto);

        return ResponseEntity.ok(true);
    }
}
