package com.andreiharpa.assignment.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateProductDto {
    @NotNull()
    @Size(max = 50)
    private String name;

    @NotNull()
    @Positive()
    private BigDecimal price;
}
