package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NotFoundElementExceptionTest {

    @Test
    void messageTest() {
        String message = "Exception";
        Exception e = assertThrows(NotFoundElementException.class, () -> {
            throw new NotFoundElementException(message);
        });
        assertEquals(message, e.getMessage());
    }
}
