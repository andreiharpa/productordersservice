package com.andreiharpa.assignment.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private UUID id;

    private String customerEmail;

    @JsonFormat(pattern = TIMESTAMP_FORMAT)
    private Date timestamp;

    private BigDecimal totalPrice;

    private List<ProductDto> products;
}
