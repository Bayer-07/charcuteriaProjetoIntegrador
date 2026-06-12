package com.example.charcuteria.unit.subscription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.subscription.SubscriptionController;
import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.subscription.SubscriptionPlanService;
import com.example.charcuteria.service.subscription.SubscriptionService;

@WebMvcTest(SubscriptionController.class)
@Import({SecurityConfig.class, SubscriptionControllerTests.MockViewResolverConfig.class})
public class SubscriptionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionPlanService subscriptionPlanService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // Configuração para interceptar a resolução do Thymeleaf e simular uma View válida
    @TestConfiguration
    static class MockViewResolverConfig {
        @Bean
        public ViewResolver mockViewResolver() {
            return new ViewResolver() {
                @Override
                public View resolveViewName(String viewName, Locale locale) throws Exception {
                    return new AbstractUrlBasedView() {
                        @Override
                        protected void renderMergedOutputModel(
                                java.util.Map<String, Object> model, 
                                jakarta.servlet.http.HttpServletRequest request, 
                                jakarta.servlet.http.HttpServletResponse response) throws Exception {
                            // Não faz nada, apenas simula sucesso na renderização
                        }
                    };
                }
            };
        }
    }

    @Test
    void testListSubscriptions() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                mockUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(subscriptionPlanService.returnAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/subscriptions")
                .with(authentication(auth)))
            .andExpect(status().isOk())
            .andExpect(view().name("public/subscriptions-options"))
            .andExpect(model().attributeExists("plans"));

        verify(subscriptionPlanService).returnAll();
    }

    @Test
    @WithMockUser
    void testUpdate() throws Exception {
        mockMvc.perform(post("/subscriptions/update/1")
                .with(csrf())
                .param("userId", "10")
                .param("planId", "5")
                .param("status", "ACTIVE"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).update(eq(1), any(SubscriptionRequest.class));
    }

    @Test
    @WithMockUser
    void testDelete() throws Exception {
        mockMvc.perform(post("/subscriptions/delete/1")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subscriptions"));

        verify(subscriptionService).deleteById(1);
    }
}