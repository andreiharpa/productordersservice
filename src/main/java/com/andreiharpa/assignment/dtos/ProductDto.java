package com.andreiharpa.assignment.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import com.andreiharpa.assignment.services.ProductService;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto{
    private UUID id;
    private String name;
    private BigDecimal price;
}
