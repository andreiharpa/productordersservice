package com.andreiharpa.assignment.controllers;

import com.andreiharpa.assignment.dtos.FieldErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Controller advice for handling validation errors
 *
 * @author Andrei Harpa
 *
 */
@ControllerAdvice
public class ValidationErrorsControllerAdvice {
    /**
     * Handles {@link ConstraintViolationException}
     *
     * @param e the exception
     * @return a list of {@link FieldErrorDto} containing the validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<FieldErrorDto> onConstraintValidationException(ConstraintViolationException e) {
        List<FieldErrorDto> errors = new ArrayList<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errors.add(new FieldErrorDto(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return errors;
    }

    /**
     * Handles {@link MethodArgumentNotValidException}
     *
     * @param e the exception
     * @return a list of {@link FieldErrorDto} containing the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<FieldErrorDto> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<FieldErrorDto> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return errors;
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException}
     *
     * @param e the exception
     * @return a list of {@link FieldErrorDto} containing the validation errors
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    FieldErrorDto onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new FieldErrorDto(e.getName(), e.getMessage());
    }
}
