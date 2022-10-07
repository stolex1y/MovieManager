package ru.stolexiy.server.commands;

import ru.stolexiy.data.*;
import ru.stolexiy.server.MovieManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, описывающий команду, которая возвращает всех операторов в порядке возрастания
 */
public class PrintFieldAscendingOperatorCommand extends AbstractCommand<Nothing, List<Person>> {

    public PrintFieldAscendingOperatorCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public List<Person> execute(User user) {
        return movieManager
                .getMovies()
                .values()
                .stream()
                .map(Movie::getDirector)
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }
}
