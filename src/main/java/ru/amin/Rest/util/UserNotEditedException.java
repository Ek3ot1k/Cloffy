package ru.amin.Rest.util;

public class UserNotEditedException extends RuntimeException {
    public UserNotEditedException(String message) {
        super(message);
    }
}
