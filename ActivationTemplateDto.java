package com.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivationTemplateDto {
    private String name;
    private String surname;
    private String companyName;
    private String mail;
    private String phoneNumber;
    private String activationCode;
}
