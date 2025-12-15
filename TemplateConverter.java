package com.mail;

import com.dto.ActivationTemplateDto;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateConverter {

    public String loadAndFillActivationTemplate(ActivationTemplateDto templateDto) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/account_activation.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        html = html.replace("{{name}}", templateDto.getName())
                .replace("{{surname}}", templateDto.getSurname())
                .replace("{{jobTitle}}", templateDto.getCompanyName())
                .replace("{{mail}}", templateDto.getMail())
                .replace("{{phoneNumber}}", templateDto.getPhoneNumber())
                .replace("{{activationCode}}", templateDto.getActivationCode());

        return html;
    }

    public String loadAndFillNegativeActivationTemplate(ActivationTemplateDto templateDto) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/account_rejection.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        html = html.replace("{{mail}}", templateDto.getMail())
                .replace("{{phoneNumber}}", templateDto.getPhoneNumber());

        return html;
    }
}
