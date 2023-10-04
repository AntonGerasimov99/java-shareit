package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DuplicatedExceptionTest {

    @Test
    void messageTest() {
        String message = "Exception";
        Exception e = assertThrows(DuplicatedException.class, () -> {
            throw new DuplicatedException(message);
        });
        assertEquals(message, e.getMessage());
    }
}
