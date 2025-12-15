package com.service;

import com.dto.ActivationTemplateDto;
import com.dto.EmailDetails;
import com.enums.ErrorTypeEnum;
import com.enums.MailActionEnum;
import com.mail.SendMail;
import com.mail.TemplateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final TemplateConverter templateConverter = new TemplateConverter();
    private final SendMail sendMail;
    private final ErrorHandlingService errorHandlingService;

    @Override
    public void sendMailWithAttachment(EmailDetails details) {
    }

    @Override
    public void sendActivationEmail(EmailDetails details, ActivationTemplateDto templateDto) {
        try {
            String htmlContent = templateConverter.loadAndFillActivationTemplate(templateDto);
            sendMail.sendMailMethod(details.getSubject(), htmlContent, details.getRecipient(), MailActionEnum.ACTIVATION_MAIL.getValue());

        } catch (IOException e) {
            errorHandlingService.saveErrorIntoDatabase(
                    "Error sending activation email",
                    ErrorTypeEnum.EMAIL_ERROR.getDescription(),
                    ErrorTypeEnum.EMAIL_ERROR);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendFailActivationEmail(EmailDetails details, ActivationTemplateDto templateDto) {
        try {
            String htmlContent = templateConverter.loadAndFillNegativeActivationTemplate(templateDto);
            sendMail.sendMailMethod(details.getSubject(), htmlContent, details.getRecipient(), MailActionEnum.ACTIVATION_MAIL.getValue());

        } catch (IOException e) {
            errorHandlingService.saveErrorIntoDatabase(
                    "Error sending deactivation email",
                    ErrorTypeEnum.EMAIL_ERROR.getDescription(),
                    ErrorTypeEnum.EMAIL_ERROR);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}