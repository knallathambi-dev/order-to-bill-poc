// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.util.CurrencyUtils;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

@Service
public final class ProductPriceNormalizationService {

    private ProductPriceNormalizationService() {}

    /**
     * Normalize all products before saving (POST/PATCH).
     * Rounds  downPayment, dutyFreeAmount, taxIncludedAmount.
     */
    public static void normalizeBeforeSave(Collection<ProductEntity> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        for (ProductEntity product : products) {
            normalizeProduct(product);
        }
    }

    private static void normalizeProduct(ProductEntity product) {
        if (product.getProductPrice() == null) {
            return;
        }
        for (ProductPriceEntity priceEntity : product.getProductPrice()) {

            if (priceEntity.getDownPayment() != null) {
                int fractionDigits = getFractionDigits(priceEntity.getPrice());
                priceEntity.setDownPayment(BigDecimal.valueOf(priceEntity.getDownPayment()).setScale(fractionDigits, RoundingMode.HALF_UP).floatValue());
            }

            normalizePrice(priceEntity.getPrice());

            if (priceEntity.getProductPriceAlteration() != null) {
                for (PriceAlterationEntity alteration : priceEntity.getProductPriceAlteration()) {
                    normalizePrice(alteration.getPrice());
                }
            }
        }
    }

    private static int getFractionDigits(PriceEntity price) {
        if (price == null) {
            return 2;
        }

        MoneyEntity money = price.getTaxIncludedAmount() != null
                ? price.getTaxIncludedAmount()
                : price.getDutyFreeAmount();

        if (money == null || money.getUnit() == null) {
            return 2;
        }

        return CurrencyUtils.roundByCurrency(1.0, money.getUnit()).scale();
    }
    private static void normalizePrice(PriceEntity price) {
        if (price == null) {
            return;
        }

        normalizeMoney(price.getDutyFreeAmount());
        normalizeMoney(price.getTaxIncludedAmount());
    }

    private static void normalizeMoney(MoneyEntity money) {
        if (money == null) {
            return;
        }


        if (money.getUnit() == null) {
            money.setUnit("EUR");
        }

        if (money.getValue() == null) {
            money.setValue(0.0f);
            return;
        }

        BigDecimal rounded = CurrencyUtils.roundByCurrency(money.getValue(), money.getUnit());
        money.setValue(rounded.floatValue());
    }
}