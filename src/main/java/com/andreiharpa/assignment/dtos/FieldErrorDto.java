package com.andreiharpa.assignment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FieldErrorDto {
    private final String fieldName;
    private final String message;
}
