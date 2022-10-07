package ru.stolexiy.connection;

import java.io.Serializable;

/**
 * Класс для объединения имени команды и ее аргумента
 */
public class CommandWithArgument implements Serializable {
    String name;
    Object argument;

    public CommandWithArgument(String name, Object argument) {
        this.name = name;
        this.argument = argument;
    }

    public String getName() {
        return name;
    }

    public Object getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return "CommandWithArgument{" +
                "name='" + name + '\'' +
                ", argument=" + argument +
                '}';
    }
}
