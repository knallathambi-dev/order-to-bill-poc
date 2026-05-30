package com.orange.discobole.productinventory.util;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Slf4j
public class CurrencyUtils {

    private static final String MGA = "MGA";

    /**
     * Rounds a value according to the currency's fraction digits using HALF_UP.
     * @param amount value to round
     * @param currencyCode ISO 4217 currency code (e.g., EUR, USD)
     * @return rounded BigDecimal
     */
    public static BigDecimal roundByCurrency(double amount, String currencyCode) {
        if (currencyCode == null) {
            throw new IllegalArgumentException("Currency code must not be null");
        }
        int fractionDigits;

        if (MGA.equals(currencyCode)) {
            fractionDigits = 0;
        } else {
            Currency currency = Currency.getInstance(currencyCode);
            fractionDigits = currency.getDefaultFractionDigits();
        }

        return BigDecimal.valueOf(amount).setScale(fractionDigits, RoundingMode.HALF_UP);

    }
}