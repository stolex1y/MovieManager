package ru.stolexiy.client.ui.view;

import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.*;

public class ResourceLoader {

    private static final Map<Pair<String, Integer>, Font> fontCache = new HashMap<>();
    private static final List<Pair<String, Locale>> languages = new ArrayList<>();

    static {
        languages.add(new Pair<>("Русский", new Locale("ru", "RU")));
        languages.add(new Pair<>("Український", new Locale("uk", "UK")));
        languages.add(new Pair<>("Norsk", new Locale("no", "NO")));
        languages.add(new Pair<>("English", new Locale("en", "EN")));
    }

    public static Font loadFont(String fontFamily, int size) {
        Pair<String, Integer> key = new Pair<>(fontFamily, size);
        Font foundFont = fontCache.get(key);
        if (foundFont != null)
            return fontCache.get(key);
        Font loadedFont = Font.loadFont(ResourceLoader.class.getResourceAsStream("/fonts/" + fontFamily + ".ttf"), size);
        fontCache.put(key, loadedFont);
        return loadedFont;
    }

    public static String loadStylesheet(String stylesheet) {
        return Objects.requireNonNull(ResourceLoader.class.getResource("/styles/" + stylesheet + ".css")).toExternalForm();
    }

    private static ResourceBundle loadResourceBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }

    public static ResourceBundle loadResourceBundle() {
        return loadResourceBundle("strings.strings", Locale.getDefault());
    }

    public static ResourceBundle loadResourceBundle(Locale locale) {
        return loadResourceBundle("strings.strings", locale);
    }

    public static List<Pair<String, Locale>> getAllLanguages() {
        return languages;
    }


}
