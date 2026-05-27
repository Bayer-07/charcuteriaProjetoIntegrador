package com.example.charcuteria.unit.subscriptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.subscription.SubscriptionController;
import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.SubscriptionResponse;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.subscription.SubscriptionPlanService;
import com.example.charcuteria.service.subscription.SubscriptionService;

@WebMvcTest(SubscriptionController.class)
@Import(SecurityConfig.class)
public class SubscriptionsTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionPlanService planService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("user@test.com");
        testUser.setRole(UserRoleEnum.CUSTOMER);
    }

    @Test
    void testListSubscriptions_Authenticated() throws Exception {
        List<SubscriptionResponse> subscriptions = Arrays.asList(
            createSubscriptionResponse(1, "ACTIVE"),
            createSubscriptionResponse(2, "PAUSED")
        );

        when(subscriptionService.returnByUserId(1)).thenReturn(subscriptions);

        mockMvc.perform(get("/subscriptions")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("public/subscriptions-options"))
            .andExpect(model().attributeExists("plans"));
    }

    @Test
    void testListSubscriptions_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/subscriptions"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }


    @Test
    void testUpdate_Success() throws Exception {
        mockMvc.perform(post("/subscriptions/update/5")
                .with(user(testUser))
                .param("userId", "1")
                .param("planId", "2")
                .param("status", "PAUSED"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).update(anyInt(), any(SubscriptionRequest.class));
    }

    @Test
    void testDelete_Success() throws Exception {
        mockMvc.perform(post("/subscriptions/delete/10")
                .with(user(testUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).deleteById(10);
    }

    // Alta prioridade - CRUD gaps e segurança
    @Test
    void testGetById_NotFound() throws Exception {
        when(subscriptionService.returnById(999))
            .thenThrow(new RuntimeException("Assinatura não encontrada"));

        // Exception não é tratada, ServletException
        try {
            mockMvc.perform(get("/subscriptions/999")
                    .with(user(testUser)));
        } catch (Exception e) {
            // Expected exception
        }

        verify(subscriptionService).returnById(999);
    }

    @Test
    void testUpdate_InvalidStatus() throws Exception {
        mockMvc.perform(post("/subscriptions/update/5")
                .with(user(testUser))
                .param("userId", "1")
                .param("planId", "2")
                .param("status", "INVALID_STATUS"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).update(anyInt(), any(SubscriptionRequest.class));
    }

    @Test
    void testListSubscriptions_MultipleActiveSubscriptions() throws Exception {
        List<SubscriptionResponse> subscriptions = Arrays.asList(
            createSubscriptionResponse(1, "ACTIVE"),
            createSubscriptionResponse(2, "ACTIVE"),
            createSubscriptionResponse(3, "PAUSED")
        );

        when(subscriptionService.returnByUserId(1)).thenReturn(subscriptions);

        mockMvc.perform(get("/subscriptions")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("public/subscriptions-options"))
            .andExpect(model().attributeExists("plans"));
    }

    // Média prioridade - Status transitions
    @Test
    void testUpdate_StatusTransition_ActiveToPaused() throws Exception {
        mockMvc.perform(post("/subscriptions/update/5")
                .with(user(testUser))
                .param("userId", "1")
                .param("planId", "2")
                .param("status", "PAUSED"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).update(anyInt(), any(SubscriptionRequest.class));
    }

    @Test
    void testUpdate_StatusTransition_PausedToCancelled() throws Exception {
        mockMvc.perform(post("/subscriptions/update/5")
                .with(user(testUser))
                .param("userId", "1")
                .param("planId", "2")
                .param("status", "CANCELLED"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).update(anyInt(), any(SubscriptionRequest.class));
    }

    private SubscriptionResponse createSubscriptionResponse(Integer id, String status) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(id);
        response.setUserId(1);
        response.setPlanId(2);
        response.setPlanName("Plano Teste");
        response.setStatus(status);
        response.setPrice(99.90);
        response.setUserName("Test User");
        response.setUserEmail("user@test.com");
        response.setStartedAt("2026-05-01");
        return response;
    }
}
