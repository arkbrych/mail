package com.service;

import com.dao.UserRepository;
import com.dao.VerificationTokenRepository;
import com.dto.ActivationTemplateDto;
import com.dto.EmailDetails;
import com.entity.User;
import com.entity.VerificationToken;
import com.enums.ErrorTypeEnum;
import com.openapi.model.PasswordData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ErrorHandlingService errorHandlingService;

    private final KeycloakService keycloakService;

    @Transactional
    public Boolean assignCaretaker(Long userId, String caretaker) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            UserRepresentation admin = keycloakService.getUser(caretaker);

            if (admin != null) {
                userRepository.assignCaretakerToUser(userId, caretaker);
                User userData = userRepository.getReferenceById(userId);

                VerificationToken token = verificationTokenRepository.save(VerificationToken.builder()
                        .token(UUID.randomUUID().toString())
                        .email(userData.getContactInfo().getEmail())
                        .build());

                String urlToken = "https://adres.com.pl/password/new?token=%s".formatted(token.getToken());

                emailService.sendActivationEmail(
                        EmailDetails
                                .builder()
                                .recipient(List.of(userData.getContactInfo().getEmail()))
                                .subject("Aktywacja konta")
                                .build(),
                        ActivationTemplateDto
                                .builder()
                                .name(userData.getContactInfo().getName())
                                .phoneNumber(userData.getContactInfo().getPhone())
                                .surname(userData.getContactInfo().getSurname())
                                .mail(userData.getContactInfo().getEmail())
                                .companyName(userData.getCompanyInfo().getName())
                                .activationCode(urlToken)
                                .build()
                );
            } else {
                errorHandlingService.saveErrorIntoDatabase(
                        "Nie można znaleźć opiekuna" + caretaker,
                        ErrorTypeEnum.ASSIGN_CARETAKER.getDescription(),
                        ErrorTypeEnum.ASSIGN_CARETAKER);
                throw new NoSuchElementException("Caretaker not found");
            }
        } else {
            errorHandlingService.saveErrorIntoDatabase(
                    "Nie można znaleźć klienta",
                    ErrorTypeEnum.ASSIGN_CARETAKER.getDescription(),
                    ErrorTypeEnum.ASSIGN_CARETAKER);
            throw new NoSuchElementException("Cannot find user with id " + userId);
        }
        return true;
    }

    @Transactional
    public Boolean rejectActivation(Long userId) {
        User userData = userRepository.getReferenceById(userId);
        if (userData != null) {

            userRepository.deleteUserById(userId);

            emailService.sendFailActivationEmail(
                    EmailDetails
                            .builder()
                            .recipient(List.of(userData.getContactInfo().getEmail()))
                            .subject("Brak potwierdzenia aktywacji konta")
                            .build(),
                    ActivationTemplateDto
                            .builder()
                            .phoneNumber(userData.getContactInfo().getPhone())
                            .mail(userData.getContactInfo().getEmail())
                            .build()
            );
        } else {
            throw new NoSuchElementException("Cannot find user with id " + userId);
        }
        return true;
    }

    public Boolean changePassword(PasswordData passwordData) {
        keycloakService.updateUserPassword(passwordData.getEmail(), passwordData.getPassword());
        return true;
    }
}
