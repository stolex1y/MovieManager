package ru.stolexiy.client.ui.view.controls;

import java.lang.reflect.Field;

public class SortMovie {
    private ClassField field;
    private boolean ascending;

    public SortMovie(ClassField field, boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    public Field getField() {
        return field.getField();
    }

    public Class getFieldClass() {
        return field.getClazz();
    }

    public boolean isAscending() {
        return ascending;
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

    public void setOrder(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public String toString() {
        return "SortMovie{" +
                "field=" + field +
                ", ascending=" + ascending +
                '}';
    }
}
