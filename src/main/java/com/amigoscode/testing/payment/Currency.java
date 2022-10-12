package com.amigoscode.testing.payment;

public enum Currency {
    USD("USD"),
    GBP("GBP");

    private final String value;
    Currency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean contains(Currency currency) {
        for (Currency c : Currency.values()) {
            if (c.equals(currency))
                return true;
        }
        return false;
    }
}
