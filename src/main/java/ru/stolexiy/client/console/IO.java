package ru.stolexiy.client.console;

import ru.stolexiy.data.*;

import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс, реализующий модуль ввода/вывода
 */
public class IO {
    private final Scanner in;
    private final PrintStream out;

    /**
     * @param in  поток ввода
     * @param out поток вывода
     */
    public IO(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    /**
     * Метод для считывания строки
     *
     * @return прочитанная строка
     */
    public String readString() throws NoSuchElementException {
        return in.nextLine().trim();
    }

    public boolean hasNext() {
        return in.hasNext();
    }

    /**
     * Метод для считывания не нулевой строки
     *
     * @return прочитанная строка, null, если достигнут конец потока ввода
     */
    public String readNotEmptyString() throws NoSuchElementException {
        while (true) {
            String input = readString();
            if (!input.equals(""))
                return input;
            printString("Строка не может быть пустой. Введите ещё раз: ");
        }
    }

    /**
     * Метод для считывания перечисления
     *
     * @param enumName     наименование перечисления
     * @param enumValues   массив констант данного перечисления
     * @param defaultValue значение по умолчанию
     * @param <T>          тип перечисления
     * @return прочитанное значение, либо значение по умолчанию
     */
    public <T extends Enum<T>> T readEnum(String enumName, T[] enumValues,
                                          Class<T> enumClass, T defaultValue) {
        if (enumValues.length == 0)
            throw new IllegalArgumentException();
        printString(
                "Введите " + enumName + " ("
                        + Arrays.stream(enumValues)
                        .map(Objects::toString)
                        .collect(Collectors
                                .joining(", "))
                        + "): "
        );
        while (true) {
            String input = readString();
            if (input == null || input.equals(""))
                return defaultValue;
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                printString("Некорректное значение. Введите ещё раз: ");
            }
        }
    }

    /**
     * Метод для считывания данных о режиссёре
     *
     * @return считанный объект класса Person
     */
    public Person readPerson() {
        Person person = new Person("Default");
        printString("Введите имя режиссёра: ");
        readField(person::setName, this::readNotEmptyString);
        printString("Введите рост режиссёра (например, 1.87): ");
        readField(person::setGrowthInMetres, () -> readDouble(1.7));
        readField(person::setNationality,
                () -> readEnum("место рождения режиссёра",
                        Country.values(), Country.class, null));
        printString("Введите количество снятых фильмов: ");
        readField(person::setFilmCount, () -> readInt(1));
        return person;
    }

    /**
     * Метод для считывания данных о фильме
     *
     * @return считанный объект класса Movie
     */
    public Movie readMovie() {
        Movie movie = new Movie("Default", null);
        printString("Введите название фильма: ");
        readField(movie::setName, this::readNotEmptyString);
        readField(movie::setDirector, this::readPerson);
        printString("Введите год создания фильма: ");
        readField(movie::setProductionYear, () -> readInt(Calendar.getInstance().get(Calendar.YEAR)));
        readField(movie::setCountry,
                () -> readEnum("страну производства фильма:",
                        Country.values(), Country.class, null));
        readField(movie::setGenre, () -> readEnum("жанр фильма:",
                MovieGenre.values(), MovieGenre.class, null));
        printString("Введите бюджет фильма: ");
        readField(movie::setBudget, () -> readLong(1000000L));
        printString("Введите кассовые сборы фильма: ");
        readField(movie::setFees, () -> readLong(1000000L));
        readField(movie::setMpaaRating, () -> readEnum("MPAA рейтинг фильма:",
                MpaaRating.values(), MpaaRating.class, null));
        printString("Введите продолжительность фильма в минутах: ");
        readField(movie::setDurationInMinutes, () -> readInt(120));
        printString("Введите количество оскаров фильма: ");
        readField(movie::setOscarsCount, () -> readInt(0));
        return movie;
    }

    /**
     * Метод для считывания целого числа, значение которого не может быть null
     *
     * @return считанное целое число
     */
    public int readInt() {
        while (true) {
            Integer i = readInt(null);
            if (i == null)
                printString("Некорректное значение. Введите целое число: ");
            else
                return i;
        }
    }

    /**
     * Метод для считывания числа типа int
     *
     * @param defaultValue значение по умолчанию
     * @return считанное значение
     */
    public Integer readInt(Integer defaultValue) {
        while (true) {
            String input = readString();
            if (input == null || input.equals("")) return defaultValue;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printString("Некорректное значение. Введите целое число: ");
            }
        }
    }

    /**
     * Метод для считывания числа типа long
     *
     * @param defaultValue значение по умолчанию
     * @return считанное значение
     */
    public Long readLong(Long defaultValue) {
        while (true) {
            String input = readString();
            if (input == null || input.equals("")) return defaultValue;
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                printString("Некорректное значение. Введите целое число: ");
            }
        }
    }

    /**
     * Метод для считывания числа типа double
     *
     * @param defaultValue значение по умолчанию
     * @return считанное значение
     */
    public Double readDouble(Double defaultValue) {
        while (true) {
            String input = readString();
            if (input == null || input.equals("")) return defaultValue;
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                printString("Некорректное значение. Введите число: ");
            }
        }
    }

    /**
     * Метод для считывания числа типа float
     *
     * @param defaultValue значение по умолчанию
     * @return считанное значение
     */
    public Float readFloat(Float defaultValue) {
        while (true) {
            String input = readString();
            if (input.equals("")) return defaultValue;
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                printString("Некорректное значение. Введите число: ");
            }
        }
    }

    /**
     * Метод для вывода сообщения
     *
     * @param str сообщение
     */
    public void printString(String str) {
        if (out != null)
            out.println(str);
    }

    /**
     * Метод для считывания поля
     *
     * @param setter метод для установки значения в данное поле
     * @param reader метод для считывания значения данного поля
     */
    private <T> void readField(Consumer<T> setter, Supplier<T> reader) {
        while (true) {
            try {
                setter.accept(reader.get());
                break;
            } catch (IllegalArgumentException e) {
                printString(e.getMessage());
            }
        }
    }

    public File readFileName() {
        File file = null;
        while (file == null) {
            printString("Введите имя файла: ");
            String fileName = readString();
            if (fileName == null || fileName.equals(""))
                return null;
            file = new File(fileName);
            if (!file.exists() || !file.canRead()) {
                printString("Файл не существует или не может быть прочитан.");
                file = null;
            }
        }
        return file;
    }

    public String chooseCommand(String... options) {
        String cmd = null;
        while (cmd == null) {
            printString("Выберите команду " + Arrays.toString(options) + ":");
            cmd = readNotEmptyString();
            String temp = cmd;
            if (Arrays.stream(options).noneMatch(it -> it.equals(temp))) {
                cmd = null;
            }
        }
        return cmd;
    }

    public static User readUserDataFromConsole() {
        Console console = System.console();
        if (console == null)
            return readUserData();
        String login = null, pass = null;
        while (login == null || pass == null) {
            if (login == null) {
                System.out.println("Введите логин:");
                login = console.readLine().trim();
                if (!Pattern.matches(loginPattern.pattern(), login)) {
                    System.out.println("Логин может содержать только латинские буквы, цифры и следующие символы: .-_.");
                    login = null;
                }
            } else {
                System.out.println("Введите пароль:");
                pass = new String(console.readPassword()).trim();
                if (!Pattern.matches(passPattern.pattern(), pass)) {
                    System.out.println("Пароль не может содержать пробельные символы, " +
                            "а его длина должна быть не менее 5 символов.");
                    pass = null;
                }
            }
        }
        return new User(login, pass);
    }

    private static final Pattern loginPattern = Pattern.compile("[\\d\\w.\\-_]+");
    private static final Pattern passPattern = Pattern.compile("[^\\\\s]{5,64}");

    public static User readUserData() {
        Scanner console = new Scanner(System.in);
        String login = null, pass = null;
        while (login == null || pass == null) {
            if (login == null) {
                System.out.println("Введите логин:");
                login = console.nextLine().trim();
                if (!Pattern.matches(loginPattern.pattern(), login)) {
                    System.out.println("Логин может содержать только латинские буквы, цифры и следующие символы: .-_.");
                    login = null;
                }
            } else {
                System.out.println("Введите пароль:");
                pass = console.nextLine().trim();
                if (!Pattern.matches(passPattern.pattern(), pass)) {
                    System.out.println("Пароль не может содержать пробельные символы, " +
                            "а его длина должна быть не менее 5 символов.");
                    pass = null;
                }
            }
        }
        return new User(login, pass);
    }
}
