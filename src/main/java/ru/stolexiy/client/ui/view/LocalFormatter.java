package ru.stolexiy.client.ui.view;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Currency;
import java.util.Locale;

public class LocalFormatter {
    public static NumberFormat numberFormatter(Locale locale, int fractionDigits) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(locale);
        numberFormatter.setMaximumFractionDigits(fractionDigits);
        return numberFormatter;
    }

    public static NumberFormat numberFormatter(int fractionDigits) {
        return numberFormatter(Locale.getDefault(), fractionDigits);
    }

    public static NumberFormat currencyFormatter(Locale locale) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance(Locale.US));
        currencyFormatter.setMaximumFractionDigits(0);
        return currencyFormatter;
    }

    public static NumberFormat currencyFormatter() {
        return currencyFormatter(Locale.getDefault());
    }

    public static DateTimeFormatter dateTimeFormatter(Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        return formatter.withLocale(locale);
    }

    public static DateTimeFormatter dateTimeFormatter() {
        return dateTimeFormatter(Locale.getDefault());
    }

    public static String formatDateTime(Locale locale, LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter(locale));
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(Locale.getDefault(), dateTime);
    }


    public static String formatNumber(Locale locale, Number number, int fractionDigits) {
        NumberFormat numberFormatter = numberFormatter(locale, fractionDigits);
        return numberFormatter.format(number);
    }

    public static String formatNumber(Number number, int fractionDigits) {
        return formatNumber(Locale.getDefault(), number, fractionDigits);
    }

    public static String formatMoney(Locale locale, Number number) {
        String formattedWithUSD = currencyFormatter(locale).format(number);
        formattedWithUSD = formattedWithUSD.replace("USD ", "");
        formattedWithUSD = formattedWithUSD.replace(" USD", "");
        formattedWithUSD = formattedWithUSD.replace("USD", "");
        formattedWithUSD = "$" + formattedWithUSD;
        return formattedWithUSD;
    }

    public static String formatMoney(Number number) {
        return formatMoney(Locale.getDefault(), number);
    }

}
