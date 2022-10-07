package ru.stolexiy.connection;

import java.io.Serializable;

/**
 * Типы запроса от клиента -- серверу
 */
public enum RequestType implements Serializable {
    SIGN_UP,
    SIGN_IN,
    COMMAND
}
