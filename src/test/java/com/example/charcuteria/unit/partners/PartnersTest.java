package com.example.charcuteria.unit.partners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.partner.PartnerController;
import com.example.charcuteria.dto.partner.PartnerRequest;
import com.example.charcuteria.service.partner.EmailService;
import com.example.charcuteria.service.partner.PartnerService;

@WebMvcTest(PartnerController.class)
@Import(SecurityConfig.class)
public class PartnersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerService partnerService;

    @MockBean
    private EmailService emailService;

    @Test
    void testShowForm() throws Exception {
        mockMvc.perform(get("/partners"))
            .andExpect(status().isOk())
            .andExpect(view().name("partners/partners"))
            .andExpect(model().attributeExists("partner"));
    }

    @Test
    void testSubmitForm_Success() throws Exception {
        mockMvc.perform(post("/partners")
                .with(csrf())
                .param("name", "Empresa Teste LTDA")
                .param("cnpj", "00.000.000/0001-00")
                .param("responsible", "João da Silva")
                .param("email", "teste@empresa.com")
                .param("phone", "(41) 99999-9999")
                .param("message", "Gostaríamos de ser parceiros"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("success", "Mensagem enviada!"))
            .andExpect(redirectedUrl("/partners"));

        verify(partnerService).sendForm(any(PartnerRequest.class));
    }


    @Test
    void testSubmitForm_ServiceException() throws Exception {
        doThrow(new RuntimeException("Erro ao enviar email"))
            .when(partnerService).sendForm(any(PartnerRequest.class));

        mockMvc.perform(post("/partners")
                .with(csrf())
                .param("name", "Empresa Teste")
                .param("email", "teste@empresa.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("error", "Erro ao enviar."))
            .andExpect(redirectedUrl("/partners"));
    }

    @Test
    void testSubmitForm_AllFields() throws Exception {
        mockMvc.perform(post("/partners")
                .with(csrf())
                .param("name", "Charcutaria Koch Parceiro")
                .param("cnpj", "12.345.678/0001-99")
                .param("responsible", "Maria Santos")
                .param("email", "maria@koch.com.br")
                .param("phone", "(45) 98765-4321")
                .param("message", "Mensagem completa com todos os campos preenchidos"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("success", "Mensagem enviada!"))
            .andExpect(redirectedUrl("/partners"));

        verify(partnerService).sendForm(any(PartnerRequest.class));
    }

    // Média prioridade - Validações
    @Test
    void testSubmitForm_InvalidEmailFormat() throws Exception {
        // PartnerRequest não tem @Email validation, só @NotBlank
        // Email inválido passa validação
        mockMvc.perform(post("/partners")
                .with(csrf())
                .param("name", "Empresa Teste")
                .param("email", "email-invalido"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("success", "Mensagem enviada!"));

        verify(partnerService).sendForm(any(PartnerRequest.class));
    }

    @Test
    void testSubmitForm_OnlyRequiredFields() throws Exception {
        mockMvc.perform(post("/partners")
                .with(csrf())
                .param("name", "Empresa Minima")
                .param("email", "minimo@empresa.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("success", "Mensagem enviada!"))
            .andExpect(redirectedUrl("/partners"));

        verify(partnerService).sendForm(any(PartnerRequest.class));
    }
}
