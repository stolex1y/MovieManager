package ru.stolexiy.client.ui.view.controls;

import ru.stolexiy.data.Movie;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FilterMovie {
    private ClassField field;
    private Object value;
    private String errorText;

    public FilterMovie(Field field, Class clazz, Object value) {
        this.field = new ClassField(clazz, field);
        this.value = value;
    }

    public Field getField() {
        return field.getField();
    }

    public Class getFieldClass() {
        return field.getClazz();
    }

    public Object getValue() {
        return value;
    }

    public void setField(Field field) {
        this.field.setField(field);
    }

    public void setFieldClass(Class fieldClass) {
        this.field.setClazz(fieldClass);
    }

    public void setClassField(ClassField classField) {
        this.field = classField;
    }

    public ClassField getClassField() {
        return field;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isValidValue(Movie movie) {
        String setter = "set" + getField().getName().substring(0, 1).toUpperCase() +
                getField().getName().substring(1);
        String getter = "get" + getField().getName().substring(0, 1).toUpperCase() +
                getField().getName().substring(1);
        try {
            Method mSetter = getFieldClass().getDeclaredMethod(setter, String.class);
            Method mGetter = getFieldClass().getDeclaredMethod(getter);
            mSetter.setAccessible(true);
            mGetter.setAccessible(true);
            if (getFieldClass() == Movie.class) {
                mSetter.invoke(movie, value);
                value = mGetter.invoke(movie);
            } else {
                mSetter.invoke(movie.getDirector(), value);
                value = mGetter.invoke(movie.getDirector());
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            errorText = e.getCause().getMessage();
            return false;
        }
        errorText = null;

        return true;
    }

    public String getErrorText() {
        return errorText;
    }

    @Override
    public String toString() {
        return "FilterMovie{" +
                "fieldMovie=" + getField().getName() +
                ", fieldClass=" + getFieldClass().getName() +
                ", value=" + value +
                ", errorText='" + errorText + '\'' +
                '}';
    }
}