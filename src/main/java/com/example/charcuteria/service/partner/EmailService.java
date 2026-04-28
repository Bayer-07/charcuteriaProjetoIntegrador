package com.example.charcuteria.service.partner;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.partner.PartnerRequest;

@Service

public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPartnerEmail(PartnerRequest dto) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("charcuteriakoch@gmail.com");
        message.setFrom("charcuteriakoch@gmail.com");
        message.setSubject("Nova parceria -> " + dto.getName());

        message.setText(
            "--> Nova Parceria <--\n\n" +
            "Empresa: " + dto.getName() + "\n" +
            "CNPJ: " + dto.getCnpj() + "\n" +
            "Responsável: " + dto.getResponsible() + "\n" +
            "Email: " + dto.getEmail() + "\n" +
            "Telefone: " + dto.getPhone() + "\n\n" +
            "Mensagem:\n" + dto.getMessage()
        );

        mailSender.send(message);
    }
}
