package com.example.charcuteria.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private Long id;
    private BigDecimal totalAmount;
}
