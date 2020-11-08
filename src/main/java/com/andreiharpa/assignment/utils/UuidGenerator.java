package com.andreiharpa.assignment.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Wrapper class for UUID generation
 *
 * @author Andrei Harpa
 *
 */
@Component
public class UuidGenerator {
    /**
     * Generates random {@link UUID}
     *
     * @return the generated {@link UUID}
     */
    public UUID generate() {
        return UUID.randomUUID();
    }
}
