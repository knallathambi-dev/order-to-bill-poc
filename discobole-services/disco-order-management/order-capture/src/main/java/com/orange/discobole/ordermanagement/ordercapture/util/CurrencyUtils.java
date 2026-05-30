// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.ProductPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.util.Currency;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage.INVALID_CURRENCY_CODE;

public final class CurrencyUtils {

    private static final String DEFAULT_CURRENCY = "EUR";
    private static final String MGA = "MGA";

    private CurrencyUtils() {
    }

    public static BigDecimal roundByCurrency(double amount, String currencyCode) {
        if (currencyCode == null) {
            throw new IllegalArgumentException("Currency code must not be null");
        }
        int fractionDigits;

        if (MGA.equals(currencyCode)) {
            fractionDigits = 0;
        } else {
            try {
                Currency currency = Currency.getInstance(currencyCode);
                fractionDigits = currency.getDefaultFractionDigits();
            } catch (IllegalArgumentException e) {
                throw new InvalidParameterException(INVALID_CURRENCY_CODE + ": " + currencyCode);
            }
        }
        return BigDecimal.valueOf(amount).setScale(fractionDigits, RoundingMode.HALF_UP);
    }

    public static Money roundMoney(Money money) {
        if (money == null || money.getValue() == null) {
            return money;
        }

        String currencyCode = money.getUnit() != null ? money.getUnit() : DEFAULT_CURRENCY;
        BigDecimal rounded = roundByCurrency(money.getValue().doubleValue(), currencyCode);

        Money result = new Money();
        result.setUnit(currencyCode);
        result.setValue(rounded.floatValue());
        return result;
    }

    public static com.orange.discobole.productinventory.dto.v1.Money roundMoney(
            com.orange.discobole.productinventory.dto.v1.Money money) {

        if (money == null || money.getValue() == null) {
            return money;
        }

        String currencyCode = money.getUnit() != null ? money.getUnit() : DEFAULT_CURRENCY;
        BigDecimal rounded = roundByCurrency(money.getValue().doubleValue(), currencyCode);

        com.orange.discobole.productinventory.dto.v1.Money result =
                new com.orange.discobole.productinventory.dto.v1.Money();

        result.setUnit(currencyCode);
        result.setValue(rounded.floatValue());
        return result;
    }

    public static void applyRounding(OrderPrice orderPrice) {
        if (orderPrice == null) {
            return;
        }
        applyRounding(orderPrice.getPrice());
        if (orderPrice.getPriceAlteration() != null) {
            orderPrice.getPriceAlteration().forEach(pa -> applyRounding(pa.getPrice()));
        }

        if (orderPrice.getProductOfferingPrice() == null) {
            return;
        }
        if (orderPrice.getProductOfferingPrice() instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
            productOfferingPriceCharge.setPrice(roundMoney(productOfferingPriceCharge.getPrice()));
        }

        if (orderPrice.getProductOfferingPrice() instanceof InstallmentCharge installmentCharge) {
            installmentCharge.setPrice(roundMoney(installmentCharge.getPrice()));
        }
    }

    private static void applyRounding(com.orange.discobole.productinventory.dto.v1.Price price) {
        if (price == null) {
            return;
        }

        price.setDutyFreeAmount(roundMoney(price.getDutyFreeAmount()));
        price.setTaxIncludedAmount(roundMoney(price.getTaxIncludedAmount()));
    }

    public static void applyRounding(Price price) {
        if (price == null) {
            return;
        }
        price.setDutyFreeAmount(roundMoney(price.getDutyFreeAmount()));
        price.setTaxIncludedAmount(roundMoney(price.getTaxIncludedAmount()));
    }

    public static void applyRounding(ProductPrice productPrice) {
        if (productPrice == null) {
            return;
        }

        applyRounding(productPrice.getPrice());

        if (productPrice.getProductPriceAlteration() != null) {
            productPrice.getProductPriceAlteration()
                    .forEach(pa -> applyRounding(pa.getPrice()));
        }
    }

    public static Float roundFloat(Float value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}