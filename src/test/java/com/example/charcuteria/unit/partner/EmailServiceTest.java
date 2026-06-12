package com.example.charcuteria.unit.partner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.charcuteria.dto.partner.PartnerRequest;
import com.example.charcuteria.service.partner.EmailService;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private PartnerRequest request;

    @BeforeEach
    void setUp() {
        request = new PartnerRequest();
        request.setName("Empresa Teste LTDA");
        request.setCnpj("00.000.000/0001-00");
        request.setResponsible("João da Silva");
        request.setEmail("empresa@teste.com");
        request.setPhone("(41) 99999-9999");
        request.setMessage("Gostaria de ser parceiro.");
    }

    @Test
    void testSendPartnerEmail_CallsMailSenderOnce() {
        emailService.sendPartnerEmail(request);

        verify(mailSender, times(1)).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));
    }

    @Test
    void testSendPartnerEmail_SetsCorrectRecipientAndSender() {
        ArgumentCaptor<SimpleMailMessage> captor = forClass(SimpleMailMessage.class);

        emailService.sendPartnerEmail(request);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("charcuteriakoch@gmail.com");
        assertThat(sent.getFrom()).isEqualTo("charcuteriakoch@gmail.com");
    }

    @Test
    void testSendPartnerEmail_SubjectContainsCompanyName() {
        ArgumentCaptor<SimpleMailMessage> captor = forClass(SimpleMailMessage.class);

        emailService.sendPartnerEmail(request);

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getSubject()).contains("Empresa Teste LTDA");
    }

    @Test
    void testSendPartnerEmail_BodyContainsAllDtoFields() {
        ArgumentCaptor<SimpleMailMessage> captor = forClass(SimpleMailMessage.class);

        emailService.sendPartnerEmail(request);

        verify(mailSender).send(captor.capture());
        String body = captor.getValue().getText();
        assertThat(body).contains("Empresa Teste LTDA");
        assertThat(body).contains("00.000.000/0001-00");
        assertThat(body).contains("João da Silva");
        assertThat(body).contains("empresa@teste.com");
        assertThat(body).contains("(41) 99999-9999");
        assertThat(body).contains("Gostaria de ser parceiro.");
    }

    @Test
    void testSendPartnerEmail_WhenMailSenderThrows_ExceptionPropagates() {
        doThrow(new RuntimeException("Falha no servidor SMTP"))
            .when(mailSender).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.sendPartnerEmail(request));
    }
}