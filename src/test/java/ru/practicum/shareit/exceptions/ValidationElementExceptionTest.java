package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationElementExceptionTest {
    @Test
    void messageTest() {
        String message = "Exception";
        Exception e = assertThrows(ValidationElementException.class, () -> {
            throw new ValidationElementException(message);
        });
        assertEquals(message, e.getMessage());
    }
}
