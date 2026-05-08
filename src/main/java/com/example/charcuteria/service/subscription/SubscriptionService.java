package com.example.charcuteria.service.subscription;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.SubscriptionResponse;
import com.example.charcuteria.model.Subscription;
import com.example.charcuteria.model.SubscriptionPlan;
import com.example.charcuteria.model.User;
import com.example.charcuteria.repository.subscription.SubscriptionRepository;
import com.example.charcuteria.repository.user.UserRepository;
import com.example.charcuteria.repository.subscription.SubscriptionPlanRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository repository, SubscriptionPlanRepository planRepository, UserRepository userRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public List<SubscriptionResponse> returnAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse returnById(Integer id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        return toDTO(subscription);
    }

    public List<SubscriptionResponse> returnByUserId(Integer userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse create(SubscriptionRequest request) {
        Subscription subscription = toEntity(request);
        Subscription saved = repository.save(subscription);
        return toDTO(saved);
    }

    public SubscriptionResponse update(Integer id, SubscriptionRequest request) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        subscription.setUserId(request.getUserId());
        subscription.setPlanId(request.getPlanId());
        subscription.setStatus(request.getStatus());

        Subscription updated = repository.save(subscription);
        return toDTO(updated);
    }

    public void deleteById(Integer id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));

        repository.delete(subscription);
    }

    // Mappers
    private Subscription toEntity(SubscriptionRequest dto) {
        Subscription subscription = new Subscription();
        subscription.setUserId(dto.getUserId());
        subscription.setPlanId(dto.getPlanId());
        subscription.setStatus(dto.getStatus());
        return subscription;
    }

    private SubscriptionResponse toDTO(Subscription subscription) {
        SubscriptionResponse dto = new SubscriptionResponse();
        SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));
        User user = userRepository.findById(subscription.getUserId())
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUserId());
        dto.setPlanId(subscription.getPlanId());
        dto.setUserName(user.getName());
        dto.setUserEmail(user.getEmail());
        dto.setPlanName(plan.getName());
        dto.setStatus(subscription.getStatus());
        dto.setStartedAt(subscription.getStartedAt());
        dto.setPrice(plan.getPrice());
        return dto;
    }
}
