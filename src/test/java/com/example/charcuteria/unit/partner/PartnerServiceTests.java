package com.example.charcuteria.unit.partner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.charcuteria.dto.partner.PartnerRequest;
import com.example.charcuteria.service.partner.EmailService;
import com.example.charcuteria.service.partner.PartnerService;

@ExtendWith(MockitoExtension.class)
public class PartnerServiceTests {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PartnerService partnerService;

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
    void testSendForm_DelegatesTo_EmailService() {
        partnerService.sendForm(request);

        verify(emailService, times(1)).sendPartnerEmail(request);
    }

    @Test
    void testSendForm_EmailServiceCalledWithSameRequest() {
        partnerService.sendForm(request);

        verify(emailService).sendPartnerEmail(request);
    }

    @Test
    void testSendForm_WhenEmailServiceThrows_ExceptionPropagates() {
        doThrow(new RuntimeException("Falha no envio de email")).when(emailService).sendPartnerEmail(request);

        assertThrows(RuntimeException.class, () -> partnerService.sendForm(request));
    }
}