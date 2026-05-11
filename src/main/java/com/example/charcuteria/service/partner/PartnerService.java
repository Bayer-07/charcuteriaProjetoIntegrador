package com.example.charcuteria.service.partner;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.partner.PartnerRequest;

@Service
public class PartnerService {
    private final EmailService emailService;

    public PartnerService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendForm(PartnerRequest request) {
        emailService.sendPartnerEmail(request);
    }

}
