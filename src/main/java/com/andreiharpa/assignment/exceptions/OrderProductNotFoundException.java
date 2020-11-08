package com.andreiharpa.assignment.exceptions;

public class OrderProductNotFoundException extends RuntimeException{
    public OrderProductNotFoundException(String message) {
        super(message);
    }
}
