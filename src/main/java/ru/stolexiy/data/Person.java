package ru.stolexiy.data;

import ru.stolexiy.client.ui.view.LocalFormatter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;

/**
 * Класс, определяющий режиссёра
 */
public class Person implements Comparable<Person>, Serializable {
    @Ignore
    private int id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private double growthInMetres; //Значение поля не может быть больше 3.0 или меньше 0
    private Country nationality;
    private int filmCount; //Значение поля не может быть меньше 1

    /**
     * @param name           имя
     * @param growthInMetres рост в метрах
     * @param nationality    национальность
     * @param filmCount      количество снятых фильмов
     */
    public Person(int id, String name, double growthInMetres, Country nationality, int filmCount) {
        this.id = id;
        setName(name);
        setGrowthInMetres(growthInMetres);
        setNationality(nationality);
        setFilmCount(filmCount);
    }

    public Person(Person person) {
        this(person.id, person.name, person.growthInMetres, person.nationality, person.filmCount);
    }

    /**
     * @param name имя
     */
    public Person(String name) {
        setName(name);
    }

    public int getId() {
        return id;
    }

    /**
     * Метод для задания имени режиссёра
     *
     * @param name имя режиссёра
     * @throws IllegalArgumentException если поле принимает недопустимое значение
     */
    public void setName(String name) throws IllegalArgumentException {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("error.person.name");
        this.name = name;
    }

    /**
     * Метод для задания роста режиссёра в метрах
     *
     * @param growthInMetres раст режиссёра в метрах
     * @throws IllegalArgumentException если поле принимает недопустимое значение
     */
    public void setGrowthInMetres(double growthInMetres) throws IllegalArgumentException {
        if (growthInMetres > 3.0 || growthInMetres <= 0)
            throw new IllegalArgumentException("error.person.growthInMetres");
        this.growthInMetres = growthInMetres;
    }

    public void setGrowthInMetres(String growthInMetres) throws IllegalArgumentException {
        try {
            setGrowthInMetres(LocalFormatter.numberFormatter(2).parse(growthInMetres).doubleValue());
        } catch (ParseException e) {
            throw new IllegalArgumentException("error.need.double");
        }
    }

    /**
     * Метод для задания национальности режиссёра
     *
     * @param nationality национальность режиссёра
     */
    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public void setNationality(String nationality) {
        setNationality(Enum.valueOf(Country.class, nationality));
    }

    /**
     * Метод для задания количества снятых фильмов
     *
     * @param filmCount количество снятых фильмов
     * @throws IllegalArgumentException если поле принимает недопустимое значение
     */
    public void setFilmCount(int filmCount) throws IllegalArgumentException {
        if (filmCount <= 0)
            throw new IllegalArgumentException("error.person.filmCount");
        this.filmCount = filmCount;
    }

    public void setFilmCount(String filmCount) throws IllegalArgumentException {
        try {
            setFilmCount(Integer.parseInt(filmCount));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("error.need.int");
        }
    }

    /**
     * Метод для получения имени режиссёра
     *
     * @return имя режиссёра
     */
    public String getName() {
        return name;
    }

    /**
     * Метод для получения роста режиссёра в метрах
     *
     * @return рост режиссёра в метрах
     */
    public double getGrowthInMetres() {
        return growthInMetres;
    }

    /**
     * Метод для получения национальности режиссёра
     *
     * @return национальность режиссёра
     */
    public Country getNationality() {
        return nationality;
    }

    /**
     * Метод для получения количества снятых фильмов
     *
     * @return количество снятых фильмов
     */
    public int getFilmCount() {
        return filmCount;
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", growthInMetres=" + growthInMetres +
                ", nationality=" + nationality +
                ", filmCount=" + filmCount +
                '}';
    }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name);
    }

    public void copy(Person p) {
        if (this.id != p.id)
            return;
        this.name = p.name;
        this.growthInMetres = p.growthInMetres;
        this.nationality = p.nationality;
        this.filmCount = p.filmCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Double.compare(person.growthInMetres, growthInMetres) == 0 && filmCount == person.filmCount && name.equals(person.name) && nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, growthInMetres, nationality, filmCount);
    }
}
