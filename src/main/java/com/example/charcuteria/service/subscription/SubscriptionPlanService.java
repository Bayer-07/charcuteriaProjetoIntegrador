package com.example.charcuteria.service.subscription;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.subscription.SubscriptionPlanRequest;
import com.example.charcuteria.dto.subscription.SubscriptionPlanResponse;
import com.example.charcuteria.model.SubscriptionPlan;
import com.example.charcuteria.repository.subscription.SubscriptionPlanRepository;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;

    public SubscriptionPlanService(SubscriptionPlanRepository repository) {
        this.repository = repository;
    }

    public List<SubscriptionPlanResponse> returnAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionPlanResponse returnById(Integer id) {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        return toDTO(plan);
    }

    public SubscriptionPlanResponse create(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = toEntity(request);
        SubscriptionPlan saved = repository.save(plan);
        return toDTO(saved);
    }

    public SubscriptionPlanResponse update(Integer id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());

        SubscriptionPlan updated = repository.save(plan);
        return toDTO(updated);
    }

    public void deleteById(Integer id) {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        repository.delete(plan);
    }

    // Mappers
    private SubscriptionPlan toEntity(SubscriptionPlanRequest dto) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        return plan;
    }

    private SubscriptionPlanResponse toDTO(SubscriptionPlan plan) {
        SubscriptionPlanResponse dto = new SubscriptionPlanResponse();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setIsActive(plan.getIsActive());
        return dto;
    }
}