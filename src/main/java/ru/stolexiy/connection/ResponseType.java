package ru.stolexiy.connection;

/**
 * Тип ответа сервера
 */
public enum ResponseType {
    /**
     * Успешное выполнение команды
     */
    OK,

    /**
     * Недостаточно прав для выполнения команды
     */
    ILLEGAL_ACCESS,

    /**
     * Неправильный запрос от клиента
     */
    INVALID_REQUEST,

    /**
     * Внутренняя ошибка сервера
     */
    INTERNAL_SERVER_ERROR,

    /**
     * Неуспешное выполнение команды
     */
    FAILED_EXECUTION,

    /**
     * Недоступен
     */
    NOT_AVAILABLE,

    /**
     * Ошибка аутентификации/регистрации
     */
    LOGIN_FAILED
}
