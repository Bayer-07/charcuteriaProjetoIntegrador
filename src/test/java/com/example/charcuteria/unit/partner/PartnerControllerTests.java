package com.example.charcuteria.unit.partner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.partner.PartnerController;
import com.example.charcuteria.dto.partner.PartnerRequest;
import com.example.charcuteria.service.partner.EmailService;
import com.example.charcuteria.service.partner.PartnerService;

@WebMvcTest(PartnerController.class)
@Import({SecurityConfig.class, PartnerControllerTests.ViewResolverConfig.class})
public class PartnerControllerTests {

    static class ViewResolverConfig {
        @Bean
        public ViewResolver viewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix("/WEB-INF/views/");
            resolver.setSuffix(".html");
            return resolver;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerService partnerService;

    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser
    void testForm_ReturnsPartnersView() throws Exception {
        mockMvc.perform(get("/partners"))
            .andExpect(status().isOk())
            .andExpect(view().name("public/partners"))
            .andExpect(model().attributeExists("partner"));
    }

    @Test
    @WithMockUser
    void testForm_ModelContainsEmptyPartnerRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/partners"))
            .andExpect(status().isOk())
            .andReturn();

        Object attr = result.getModelAndView().getModel().get("partner");
        assertThat(attr).isInstanceOf(PartnerRequest.class);
    }

    @Test
    @WithMockUser
    void testEnviar_Success_RedirectsWithFlashSuccess() throws Exception {
        mockMvc.perform(post("/partners")
                .param("name", "Empresa Teste LTDA")
                .param("cnpj", "00.000.000/0001-00")
                .param("responsible", "João da Silva")
                .param("email", "empresa@teste.com")
                .param("phone", "(41) 99999-9999")
                .param("message", "Gostaria de ser parceiro."))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/partners"))
            .andExpect(flash().attributeExists("success"));

        verify(partnerService).sendForm(any(PartnerRequest.class));
    }

    @Test
    @WithMockUser
    void testEnviar_ServiceThrowsException_RedirectsWithFlashError() throws Exception {
        doThrow(new RuntimeException("Falha no envio")).when(partnerService).sendForm(any(PartnerRequest.class));

        mockMvc.perform(post("/partners")
                .param("name", "Empresa Teste LTDA")
                .param("cnpj", "00.000.000/0001-00")
                .param("responsible", "João da Silva")
                .param("email", "empresa@teste.com")
                .param("phone", "(41) 99999-9999")
                .param("message", "Gostaria de ser parceiro."))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/partners"))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser
    void testTesteEmail_ReturnsEmailEnviadoString() throws Exception {
        mockMvc.perform(get("/partners/teste-email"))
            .andExpect(status().isOk())
            .andExpect(content().string("Email enviado!"));

        verify(emailService).sendPartnerEmail(any(PartnerRequest.class));
    }

    @Test
    @WithMockUser
    void testTesteEmail_CallsEmailServiceWithCorrectData() throws Exception {
        mockMvc.perform(get("/partners/teste-email"))
            .andExpect(status().isOk());

        verify(emailService).sendPartnerEmail(any(PartnerRequest.class));
    }
}