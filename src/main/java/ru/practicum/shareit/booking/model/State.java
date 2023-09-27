package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.UnknownStatusException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State checkState(String check) {
        State result;
        try {
            result = State.valueOf(check);
        } catch (IllegalArgumentException ex) {
            throw new UnknownStatusException("Unknown state: " + check);
        }
        return result;
    }
}
