package com.example.charcuteria.service.subscription;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.SubscriptionResponse;
import com.example.charcuteria.dto.subscription.UserSubscriptionResponseDto;
import com.example.charcuteria.model.Subscription;
import com.example.charcuteria.model.SubscriptionPlan;
import com.example.charcuteria.repository.subscription.SubscriptionRepository;
import com.example.charcuteria.repository.subscription.SubscriptionPlanRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionPlanRepository planRepository;

    public SubscriptionService(SubscriptionRepository repository, SubscriptionPlanRepository planRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
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

    public Optional<UserSubscriptionResponseDto> getActiveSubscriptionByUserId(Integer userId) {
        return repository.findByUserId(userId)
                .stream()
                .filter(sub -> "ACTIVE".equalsIgnoreCase(sub.getStatus()) || "PAUSED".equalsIgnoreCase(sub.getStatus()))
                .findFirst()
                .map(this::toUserDTO);
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

        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUserId());
        dto.setPlanId(subscription.getPlanId());
        dto.setPlanName(plan.getName());
        dto.setStatus(subscription.getStatus());
        dto.setStartedAt(subscription.getStartedAt());
        dto.setPrice(plan.getPrice());
        return dto;
    }

    private UserSubscriptionResponseDto toUserDTO(Subscription subscription) {
        SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        // Format date as DD/MM/YYYY
        String formattedDate = formatDate(subscription.getStartedAt());

        return new UserSubscriptionResponseDto(
            subscription.getId(),
            plan.getName(),
            plan.getDescription(),
            plan.getPrice(),
            subscription.getStatus(),
            formattedDate
        );
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        try {
            // Parse the date from database format (YYYY-MM-DD)
            LocalDate date = LocalDate.parse(dateString);
            // Format as DD/MM/YYYY
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            // If parsing fails, return the original string
            return dateString;
        }
    }
}
