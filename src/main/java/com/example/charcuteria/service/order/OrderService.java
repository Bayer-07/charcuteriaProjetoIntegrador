package com.example.charcuteria.service.order;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.order.OrderResponseDto;
import com.example.charcuteria.model.Order;
import com.example.charcuteria.repository.order.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<OrderResponseDto> findByUserId(Integer userId) {
        return repository.findByUserId(userId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public OrderResponseDto findById(Integer id) {
        Order order = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        return toDTO(order);
    }

    public List<OrderResponseDto> findAll() {
        return repository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public OrderResponseDto save(Order order) {
        Order saved = repository.save(order);
        return toDTO(saved);
    }

    public void delete(Integer id) {
        repository.delete(id);
    }

    private OrderResponseDto toDTO(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setYear(order.getOrderDate().getYear());
        return dto;
    }
}
