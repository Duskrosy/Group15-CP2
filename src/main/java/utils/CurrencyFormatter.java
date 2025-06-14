package com.motorph.utils;

public class CurrencyFormatter {
    public static String format(String value) {
        try {
            double num = Double.parseDouble(value.replace(",", "").trim());
            return String.format("₱%,.2f", num);
        } catch (Exception e) {
            return value != null ? value : "";
        }
    }

    // Overload for double
    public static String format(double value) {
        return String.format("₱%,.2f", value);
    }
}
