package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnknownStatusExceptionTest {
    @Test
    void messageTest() {
        String message = "Exception";
        Exception e = assertThrows(UnknownStatusException.class, () -> {
            throw new UnknownStatusException(message);
        });
        assertEquals(message, e.getMessage());
    }
}
