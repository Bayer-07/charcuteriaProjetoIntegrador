package com.example.charcuteria.unit.subscription;

import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.SubscriptionResponse;
import com.example.charcuteria.dto.subscription.UserSubscriptionResponseDto;
import com.example.charcuteria.model.Subscription;
import com.example.charcuteria.model.SubscriptionPlan;
import com.example.charcuteria.model.User;
import com.example.charcuteria.repository.subscription.SubscriptionPlanRepository;
import com.example.charcuteria.repository.subscription.SubscriptionRepository;
import com.example.charcuteria.repository.user.UserRepository;
import com.example.charcuteria.service.subscription.SubscriptionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository repository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService service;

    private Subscription subscription;
    private SubscriptionPlan plan;
    private User user;

    @BeforeEach
    void setUp() {
        plan = new SubscriptionPlan();
        plan.setId(1);
        plan.setName("Plano Básico");
        plan.setDescription("Descrição do plano básico");
        plan.setPrice(49.90);

        user = new User();
        user.setId(1);
        user.setName("João Silva");
        user.setEmail("joao@example.com");

        subscription = new Subscription();
        subscription.setId(1);
        subscription.setUserId(1);
        subscription.setPlanId(1);
        subscription.setStatus("ACTIVE");
        subscription.setStartedAt("2024-01-15");
    }

    // -------------------------------------------------------------------------
    // returnAll
    // -------------------------------------------------------------------------

    @Test
    void returnAll_shouldReturnMappedList() {
        when(repository.findAll()).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        List<SubscriptionResponse> result = service.returnAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getPlanName()).isEqualTo("Plano Básico");
        assertThat(result.get(0).getUserName()).isEqualTo("João Silva");
    }

    @Test
    void returnAll_shouldReturnEmptyListWhenNoSubscriptions() {
        when(repository.findAll()).thenReturn(List.of());

        List<SubscriptionResponse> result = service.returnAll();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // returnById
    // -------------------------------------------------------------------------

    @Test
    void returnById_shouldReturnSubscriptionWhenFound() {
        when(repository.findById(1)).thenReturn(Optional.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        SubscriptionResponse result = service.returnById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getUserEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void returnById_shouldThrowWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.returnById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Assinatura não encontrada");
    }

    // -------------------------------------------------------------------------
    // returnByUserId
    // -------------------------------------------------------------------------

    @Test
    void returnByUserId_shouldReturnSubscriptionsForUser() {
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        List<SubscriptionResponse> result = service.returnByUserId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1);
    }

    @Test
    void returnByUserId_shouldReturnEmptyListWhenUserHasNoSubscriptions() {
        when(repository.findByUserId(99)).thenReturn(List.of());

        List<SubscriptionResponse> result = service.returnByUserId(99);

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getActiveSubscriptionByUserId
    // -------------------------------------------------------------------------

    @Test
    void getActiveSubscriptionByUserId_shouldReturnActiveSubscription() {
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void getActiveSubscriptionByUserId_shouldReturnPausedSubscription() {
        subscription.setStatus("PAUSED");
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("PAUSED");
    }

    @Test
    void getActiveSubscriptionByUserId_shouldReturnEmptyWhenNoActiveOrPaused() {
        subscription.setStatus("CANCELLED");
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isEmpty();
    }

    @Test
    void getActiveSubscriptionByUserId_shouldReturnEmptyWhenUserHasNoSubscriptions() {
        when(repository.findByUserId(99)).thenReturn(List.of());

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(99);

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getAllActiveSubscriptionsByUserId
    // -------------------------------------------------------------------------

    @Test
    void getAllActiveSubscriptionsByUserId_shouldReturnAllSubscriptions() {
        Subscription second = new Subscription();
        second.setId(2);
        second.setUserId(1);
        second.setPlanId(1);
        second.setStatus("CANCELLED");
        second.setStartedAt("2023-06-01");

        when(repository.findByUserId(1)).thenReturn(List.of(subscription, second));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        List<UserSubscriptionResponseDto> result = service.getAllActiveSubscriptionsByUserId(1);

        assertThat(result).hasSize(2);
    }

    // -------------------------------------------------------------------------
    // updateSubscriptionStatus
    // -------------------------------------------------------------------------

    @Test
    void updateSubscriptionStatus_shouldUpdateStatusSuccessfully() {
        when(repository.findById(1)).thenReturn(Optional.of(subscription));

        service.updateSubscriptionStatus(1, 1, "PAUSED");

        assertThat(subscription.getStatus()).isEqualTo("PAUSED");
        verify(repository).save(subscription);
    }

    @Test
    void updateSubscriptionStatus_shouldThrowWhenSubscriptionNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSubscriptionStatus(99, 1, "PAUSED"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Assinatura não encontrada");
    }

    @Test
    void updateSubscriptionStatus_shouldThrowWhenUserIdDoesNotMatch() {
        when(repository.findById(1)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> service.updateSubscriptionStatus(1, 999, "PAUSED"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Acesso negado");

        verify(repository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void create_shouldPersistAndReturnSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setUserId(1);
        request.setPlanId(1);
        request.setStatus("ACTIVE");

        when(repository.save(any(Subscription.class))).thenReturn(subscription);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        SubscriptionResponse result = service.create(request);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(repository).save(any(Subscription.class));
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    void update_shouldUpdateFieldsAndReturnResponse() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setUserId(1);
        request.setPlanId(1);
        request.setStatus("CANCELLED");

        Subscription updated = new Subscription();
        updated.setId(1);
        updated.setUserId(1);
        updated.setPlanId(1);
        updated.setStatus("CANCELLED");
        updated.setStartedAt("2024-01-15");

        when(repository.findById(1)).thenReturn(Optional.of(subscription));
        when(repository.save(any(Subscription.class))).thenReturn(updated);
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        SubscriptionResponse result = service.update(1, request);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        verify(repository).save(subscription);
    }

    @Test
    void update_shouldThrowWhenSubscriptionNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        SubscriptionRequest request = new SubscriptionRequest();
        request.setUserId(1);
        request.setPlanId(1);
        request.setStatus("ACTIVE");

        assertThatThrownBy(() -> service.update(99, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Assinatura não encontrada");
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    void deleteById_shouldDeleteSuccessfully() {
        when(repository.findById(1)).thenReturn(Optional.of(subscription));

        service.deleteById(1);

        verify(repository).delete(subscription);
    }

    @Test
    void deleteById_shouldThrowWhenSubscriptionNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Assinatura não encontrada");

        verify(repository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // Date formatting (via toUserDTO indirectly)
    // -------------------------------------------------------------------------

    @Test
    void getActiveSubscriptionByUserId_shouldFormatDateCorrectly() {
        subscription.setStartedAt("2024-01-15");
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStartedAt()).isEqualTo("15/01/2024");
    }

    @Test
    void getActiveSubscriptionByUserId_shouldReturnRawStringWhenDateIsInvalid() {
        subscription.setStartedAt("not-a-date");
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStartedAt()).isEqualTo("not-a-date");
    }

    @Test
    void getActiveSubscriptionByUserId_shouldReturnEmptyStringWhenDateIsNull() {
        subscription.setStartedAt(null);
        when(repository.findByUserId(1)).thenReturn(List.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        Optional<UserSubscriptionResponseDto> result = service.getActiveSubscriptionByUserId(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStartedAt()).isEmpty();
    }
}