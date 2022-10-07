package ru.stolexiy.client.ui.view.controls;

import java.lang.reflect.Field;
import java.util.Objects;

public class ClassField {
    private Class clazz;
    private Field field;

    public ClassField(Class clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassField that = (ClassField) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, field);
    }

    @Override
    public String toString() {
        return "ClassField{" +
                "clazz=" + clazz +
                ", field=" + field.getName() +
                '}';
    }
}
