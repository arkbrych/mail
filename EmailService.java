package com.service;


import com.dto.ActivationTemplateDto;
import com.dto.EmailDetails;

public interface EmailService {

    void sendMailWithAttachment(EmailDetails details);

    void sendActivationEmail(EmailDetails details, ActivationTemplateDto templateDto);

    void sendFailActivationEmail(EmailDetails details, ActivationTemplateDto templateDto);
}
