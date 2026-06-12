package com.example.charcuteria.unit.shipping;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.shipping.ShippingController;
import com.example.charcuteria.dto.shipping.CepValidateResponse;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.shipping.ShippingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@WebMvcTest(ShippingController.class)
@Import(SecurityConfig.class)
public class ShippingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShippingService shippingService;

    @Test
    void testCalculateShipping_Success() throws Exception {
        record ShippingCalculateRequest(String cep) {}
        ShippingCalculateRequest request = new ShippingCalculateRequest("85900-000");

        // Cria a sua entidade real e define o ID que o controller vai ler
        User mockUser = new User();
        mockUser.setId(1);

        // Cria o Token de autenticação injetando o seu mockUser como a identidade Principal
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                mockUser, 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(shippingService.calculateShipping(anyString(), anyInt())).thenReturn(25.50);

        mockMvc.perform(post("/api/shipping/calculate")
                .with(csrf())
                .with(authentication(auth)) // injeta o token no SecurityContext da requisição
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(25.50));

        verify(shippingService).calculateShipping("85900000", 1);
    }

    @Test
    @WithMockUser
    void testCalculateShipping_InvalidCep() throws Exception {
        record ShippingCalculateRequest(String cep) {}
        ShippingCalculateRequest request = new ShippingCalculateRequest("123");

        mockMvc.perform(post("/api/shipping/calculate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("CEP inválido"));
    }

    @Test
    void testCalculateShipping_ThrowsException_ReturnsBadGateway() throws Exception {
        record ShippingCalculateRequest(String cep) {}
        ShippingCalculateRequest request = new ShippingCalculateRequest("85900-000");

        User mockUser = new User();
        mockUser.setId(1);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                mockUser, 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(shippingService.calculateShipping(anyString(), anyInt())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/shipping/calculate")
                .with(csrf())
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadGateway())
            .andExpect(content().string("Erro ao calcular frete"));
    }

    @Test
    @WithMockUser
    void testValidateCep_Success() throws Exception {
        record CepValidateRequest(String cep) {}
        CepValidateRequest request = new CepValidateRequest("85900-000");
        CepValidateResponse mockResponse = new CepValidateResponse(true, "OK");

        when(shippingService.validateCep("85900000")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/shipping/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.message").value("OK"));

        verify(shippingService).validateCep("85900000");
    }

    @Test
    @WithMockUser
    void testValidateCep_InvalidCepLength() throws Exception {
        record CepValidateRequest(String cep) {}
        CepValidateRequest request = new CepValidateRequest("12345");

        mockMvc.perform(post("/api/shipping/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.message").value("CEP_INVALIDO"));
    }
}