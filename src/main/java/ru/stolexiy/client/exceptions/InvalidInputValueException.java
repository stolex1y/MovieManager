package ru.stolexiy.client.exceptions;

public class InvalidInputValueException extends RuntimeException {

    private final String fieldId;

    public InvalidInputValueException(String id) {
        fieldId = id;
    }

    public String getFieldId() {
        return fieldId;
    }
}
