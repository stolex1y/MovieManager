package ru.stolexiy.data;

import java.util.function.Consumer;

public class Validator {

    private final Consumer<String> fromString;
//    private final Consumer<T> fromT;
//    private final Supplier<T> getter;

    private Exception exception = null;

    /*public Validator(Consumer<String> fromString, Consumer<T> fromT, Supplier<T> getter) {
        this.fromString = fromString;
        this.fromT = fromT;
        this.getter = getter;
    }
*/
    public Validator(Consumer<String> fromString) {
        this.fromString = fromString;
    }
    /*public boolean isValidFieldValue(String value) {
        try {
            T old = getter.get();
            fromString.accept(value);
            fromT.accept(old);
            exception = null;
            return true;
        } catch (IllegalArgumentException e) {
            exception = e;
            return false;
        }
    }*/

    public boolean vaildateAndSet(String value) {
        try {
            fromString.accept(value);
            exception = null;
            return true;
        } catch (IllegalArgumentException e) {
            exception = e;
            return false;
        }
    }

    public Exception getException() {
        return exception;
    }
}
