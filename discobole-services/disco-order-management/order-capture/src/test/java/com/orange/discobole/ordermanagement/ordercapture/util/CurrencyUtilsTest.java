// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyUtilsTest {

    @Test
    void roundByCurrency_EUR_shouldRoundToTwoDecimals() {
        BigDecimal result = CurrencyUtils.roundByCurrency(10.556, "EUR");
        assertThat(result).isEqualByComparingTo("10.56");
    }

    @Test
    void roundByCurrency_JPY_shouldRoundToZeroDecimals() {
        BigDecimal result = CurrencyUtils.roundByCurrency(10.6, "JPY");
        assertThat(result).isEqualByComparingTo("11");
    }


    @Test
    void roundByCurrency_invalidCurrency_shouldThrowException() {
        InvalidParameterException exception = assertThrows(
                InvalidParameterException.class,
                () -> CurrencyUtils.roundByCurrency(10.555, "INVALID")
        );

        assertThat(exception.getMessage()).contains("Invalid Currency code: INVALID");
    }

    @Test
    void roundByCurrency_nullCurrency_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyUtils.roundByCurrency(10.5, null));
    }

    @Test
    void roundMoney_withNullMoney_shouldReturnNull() {
        Money result = CurrencyUtils.roundMoney((Money) null);

        assertThat(result).isNull();
    }

    @Test
    void roundMoney_withNullValue_shouldReturnSameMoney() {
        Money money = new Money();
        money.setUnit("EUR");

        Money result = CurrencyUtils.roundMoney(money);

        assertThat(result).isSameAs(money);
    }

    @Test
    void roundMoney_withoutCurrency_shouldUseDefaultEUR() {
        Money money = new Money();
        money.setValue(10.555f);

        Money result = CurrencyUtils.roundMoney(money);

        assertThat(result.getUnit()).isEqualTo("EUR");
        assertThat(result.getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_shouldRoundOrderPricePrice() {
        Money dutyFree = new Money();
        dutyFree.setUnit("EUR");
        dutyFree.setValue(10.555f);
        Price price = new Price();
        price.setDutyFreeAmount(dutyFree);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setPrice(price);
        CurrencyUtils.applyRounding(orderPrice);
        assertThat(orderPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
    }


    @Test
    void applyRounding_onOrderPrice_shouldRoundPriceAlterations() {
        Money alterationMoney = new Money();
        alterationMoney.setUnit("EUR");
        alterationMoney.setValue(10.555f);
        Price alterationPrice = new Price();
        alterationPrice.setDutyFreeAmount(alterationMoney);
        PriceAlteration alteration = new PriceAlteration();
        alteration.setPrice(alterationPrice);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setPriceAlteration(List.of(alteration));
        CurrencyUtils.applyRounding(orderPrice);
        assertThat(orderPrice.getPriceAlteration().get(0).getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_onProductPrice_shouldRoundPriceAndAlterations() {
        com.orange.discobole.productinventory.dto.v1.Money money =
                new com.orange.discobole.productinventory.dto.v1.Money();
        money.setUnit("EUR");
        money.setValue(10.555f);

        com.orange.discobole.productinventory.dto.v1.Price price =
                new com.orange.discobole.productinventory.dto.v1.Price();
        price.setDutyFreeAmount(money);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setPrice(price);

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_onProductPrice_withNull_shouldDoNothing() {
        assertDoesNotThrow(
                () -> CurrencyUtils.applyRounding((com.orange.discobole.productinventory.dto.v1.ProductPrice) null)
        );
    }

    @Test
    void applyRounding_onProductPrice_withNullPrice_shouldNotThrow() {
        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setPrice(null);

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getPrice()).isNull();
    }

    @Test
    void applyRounding_onProductPrice_shouldRoundTaxIncludedAmount() {
        com.orange.discobole.productinventory.dto.v1.Money money =
                new com.orange.discobole.productinventory.dto.v1.Money();
        money.setUnit("EUR");
        money.setValue(20.555f);

        com.orange.discobole.productinventory.dto.v1.Price price =
                new com.orange.discobole.productinventory.dto.v1.Price();
        price.setTaxIncludedAmount(money);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setPrice(price);

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getPrice().getTaxIncludedAmount().getValue()).isEqualTo(20.56f);
    }

    @Test
    void applyRounding_onProductPrice_shouldRoundPriceAlteration() {
        com.orange.discobole.productinventory.dto.v1.Money alterationMoney =
                new com.orange.discobole.productinventory.dto.v1.Money();
        alterationMoney.setUnit("EUR");
        alterationMoney.setValue(15.555f);

        com.orange.discobole.productinventory.dto.v1.Price alterationPrice =
                new com.orange.discobole.productinventory.dto.v1.Price();
        alterationPrice.setDutyFreeAmount(alterationMoney);

        com.orange.discobole.productinventory.dto.v1.PriceAlteration alteration =
                new com.orange.discobole.productinventory.dto.v1.PriceAlteration();
        alteration.setPrice(alterationPrice);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setProductPriceAlteration(List.of(alteration));

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getProductPriceAlteration().get(0).getPrice().getDutyFreeAmount().getValue())
                .isEqualTo(15.56f);
    }

    @Test
    void applyRounding_onProductPrice_shouldRoundPriceAlteration_withMGA() {
        com.orange.discobole.productinventory.dto.v1.Money alterationMoney =
                new com.orange.discobole.productinventory.dto.v1.Money();
        alterationMoney.setUnit("MGA");
        alterationMoney.setValue(5.9f);

        com.orange.discobole.productinventory.dto.v1.Price alterationPrice =
                new com.orange.discobole.productinventory.dto.v1.Price();
        alterationPrice.setDutyFreeAmount(alterationMoney);

        com.orange.discobole.productinventory.dto.v1.PriceAlteration alteration =
                new com.orange.discobole.productinventory.dto.v1.PriceAlteration();
        alteration.setPrice(alterationPrice);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setProductPriceAlteration(List.of(alteration));

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getProductPriceAlteration().get(0).getPrice().getDutyFreeAmount().getValue())
                .isEqualTo(6f);
    }

    @Test
    void applyRounding_onProductPrice_withNullPriceAlteration_shouldNotThrow() {
        com.orange.discobole.productinventory.dto.v1.Money money =
                new com.orange.discobole.productinventory.dto.v1.Money();
        money.setUnit("EUR");
        money.setValue(10.555f);

        com.orange.discobole.productinventory.dto.v1.Price price =
                new com.orange.discobole.productinventory.dto.v1.Price();
        price.setDutyFreeAmount(money);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setPrice(price);
        productPrice.setProductPriceAlteration(null);

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_onProductPrice_withAllFieldsSet_shouldRoundAll() {
        // price
        com.orange.discobole.productinventory.dto.v1.Money dutyFree =
                new com.orange.discobole.productinventory.dto.v1.Money();
        dutyFree.setUnit("EUR");
        dutyFree.setValue(10.555f);

        com.orange.discobole.productinventory.dto.v1.Money taxIncluded =
                new com.orange.discobole.productinventory.dto.v1.Money();
        taxIncluded.setUnit("EUR");
        taxIncluded.setValue(12.555f);

        com.orange.discobole.productinventory.dto.v1.Price price =
                new com.orange.discobole.productinventory.dto.v1.Price();
        price.setDutyFreeAmount(dutyFree);
        price.setTaxIncludedAmount(taxIncluded);

        // priceAlteration
        com.orange.discobole.productinventory.dto.v1.Money alterationMoney =
                new com.orange.discobole.productinventory.dto.v1.Money();
        alterationMoney.setUnit("EUR");
        alterationMoney.setValue(11.555f);

        com.orange.discobole.productinventory.dto.v1.Price alterationPrice =
                new com.orange.discobole.productinventory.dto.v1.Price();
        alterationPrice.setDutyFreeAmount(alterationMoney);

        com.orange.discobole.productinventory.dto.v1.PriceAlteration alteration =
                new com.orange.discobole.productinventory.dto.v1.PriceAlteration();
        alteration.setPrice(alterationPrice);

        com.orange.discobole.productinventory.dto.v1.ProductPrice productPrice =
                new com.orange.discobole.productinventory.dto.v1.ProductPrice();
        productPrice.setPrice(price);
        productPrice.setProductPriceAlteration(List.of(alteration));

        CurrencyUtils.applyRounding(productPrice);

        assertThat(productPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
        assertThat(productPrice.getPrice().getTaxIncludedAmount().getValue()).isEqualTo(12.56f);
        assertThat(productPrice.getProductPriceAlteration().get(0).getPrice().getDutyFreeAmount().getValue())
                .isEqualTo(11.56f);
    }

    @Test
    void roundByCurrency_MGA_shouldRoundToZeroDecimals() {
        BigDecimal result = CurrencyUtils.roundByCurrency(10.9, "MGA");

        assertThat(result).isEqualByComparingTo("11");
    }

    @Test
    void roundMoney_MGA_shouldRoundToZeroDecimals() {
        Money money = new Money();
        money.setUnit("MGA");
        money.setValue(10.9f);

        Money result = CurrencyUtils.roundMoney(money);

        assertThat(result.getUnit()).isEqualTo("MGA");
        assertThat(result.getValue()).isEqualTo(11f);
    }

    @Test
    void applyRounding_shouldRoundProductOfferingPriceChargePrice_withMGA() {
        Money chargeMoney = new Money();
        chargeMoney.setUnit("MGA");
        chargeMoney.setValue(10.9f);
        ProductOfferingPriceCharge productOfferingPriceCharge = new ProductOfferingPriceCharge();
        productOfferingPriceCharge.setPrice(chargeMoney);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setProductOfferingPrice(productOfferingPriceCharge);
        CurrencyUtils.applyRounding(orderPrice);
        ProductOfferingPriceCharge result = (ProductOfferingPriceCharge) orderPrice.getProductOfferingPrice();
        assertThat(result.getPrice().getUnit()).isEqualTo("MGA");
        assertThat(result.getPrice().getValue()).isEqualTo(11f);
    }

    @Test
    void applyRounding_shouldRoundProductOfferingPriceChargePrice_withEUR() {
        Money chargeMoney = new Money();
        chargeMoney.setUnit("EUR");
        chargeMoney.setValue(10.556f);
        ProductOfferingPriceCharge productOfferingPriceCharge = new ProductOfferingPriceCharge();
        productOfferingPriceCharge.setPrice(chargeMoney);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setProductOfferingPrice(productOfferingPriceCharge);
        CurrencyUtils.applyRounding(orderPrice);
        assertThat(((ProductOfferingPriceCharge) orderPrice.getProductOfferingPrice()).getPrice().getUnit()).isEqualTo("EUR");
        assertThat(((ProductOfferingPriceCharge) orderPrice.getProductOfferingPrice()).getPrice().getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_onOrderPrice_withNullProductOfferingPrice_shouldNotThrow() {
        Money dutyFree = new Money();
        dutyFree.setUnit("EUR");
        dutyFree.setValue(10.555f);
        Price price = new Price();
        price.setDutyFreeAmount(dutyFree);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setPrice(price);
        orderPrice.setProductOfferingPrice(null);

        CurrencyUtils.applyRounding(orderPrice);

        assertThat(orderPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
    }

    @Test
    void applyRounding_shouldRoundInstallmentChargePrice_withEUR() {
        Money installmentMoney = new Money();
        installmentMoney.setUnit("EUR");
        installmentMoney.setValue(15.556f);
        InstallmentCharge installmentCharge = new InstallmentCharge();
        installmentCharge.setPrice(installmentMoney);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setProductOfferingPrice(installmentCharge);

        CurrencyUtils.applyRounding(orderPrice);

        assertThat(((InstallmentCharge) orderPrice.getProductOfferingPrice()).getPrice().getValue()).isEqualTo(15.56f);
        assertThat(((InstallmentCharge) orderPrice.getProductOfferingPrice()).getPrice().getUnit()).isEqualTo("EUR");
    }

    @Test
    void applyRounding_shouldRoundInstallmentChargePrice_withMGA() {
        Money installmentMoney = new Money();
        installmentMoney.setUnit("MGA");
        installmentMoney.setValue(15.9f);
        InstallmentCharge installmentCharge = new InstallmentCharge();
        installmentCharge.setPrice(installmentMoney);
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setProductOfferingPrice(installmentCharge);

        CurrencyUtils.applyRounding(orderPrice);

        assertThat(((InstallmentCharge) orderPrice.getProductOfferingPrice()).getPrice().getValue()).isEqualTo(16f);
        assertThat(((InstallmentCharge) orderPrice.getProductOfferingPrice()).getPrice().getUnit()).isEqualTo("MGA");
    }

    @Test
    void applyRounding_onOrderPrice_withAllFieldsSet_shouldRoundAll() {
        // price
        Money dutyFree = new Money();
        dutyFree.setUnit("EUR");
        dutyFree.setValue(10.555f);
        Money taxIncluded = new Money();
        taxIncluded.setUnit("EUR");
        taxIncluded.setValue(12.555f);
        Price price = new Price();
        price.setDutyFreeAmount(dutyFree);
        price.setTaxIncludedAmount(taxIncluded);

        // priceAlteration
        Money alterationMoney = new Money();
        alterationMoney.setUnit("EUR");
        alterationMoney.setValue(11.555f);
        Price alterationPrice = new Price();
        alterationPrice.setDutyFreeAmount(alterationMoney);
        PriceAlteration alteration = new PriceAlteration();
        alteration.setPrice(alterationPrice);

        // productOfferingPrice
        Money chargeMoney = new Money();
        chargeMoney.setUnit("EUR");
        chargeMoney.setValue(8.555f);
        ProductOfferingPriceCharge productOfferingPriceCharge = new ProductOfferingPriceCharge();
        productOfferingPriceCharge.setPrice(chargeMoney);

        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setPrice(price);
        orderPrice.setPriceAlteration(List.of(alteration));
        orderPrice.setProductOfferingPrice(productOfferingPriceCharge);

        CurrencyUtils.applyRounding(orderPrice);

        assertThat(orderPrice.getPrice().getDutyFreeAmount().getValue()).isEqualTo(10.56f);
        assertThat(orderPrice.getPrice().getTaxIncludedAmount().getValue()).isEqualTo(12.56f);
        assertThat(orderPrice.getPriceAlteration().get(0).getPrice().getDutyFreeAmount().getValue()).isEqualTo(11.56f);
        assertThat(((ProductOfferingPriceCharge) orderPrice.getProductOfferingPrice()).getPrice().getValue()).isEqualTo(8.56f);
    }

    @Test
    void roundFloat_withNull_shouldReturnNull() {
        assertThat(CurrencyUtils.roundFloat(null)).isNull();
    }

    @Test
    void roundFloat_shouldRoundUpAtHalf() {
        assertThat(CurrencyUtils.roundFloat(10.555f)).isEqualTo(10.56f);
    }

    @Test
    void roundFloat_shouldRoundDown() {
        assertThat(CurrencyUtils.roundFloat(10.554f)).isEqualTo(10.55f);
    }

    @Test
    void roundFloat_withExactTwoDecimals_shouldReturnUnchanged() {
        assertThat(CurrencyUtils.roundFloat(10.56f)).isEqualTo(10.56f);
    }

    @Test
    void roundFloat_withZero_shouldReturnZero() {
        assertThat(CurrencyUtils.roundFloat(0f)).isEqualTo(0f);
    }

    @Test
    void roundFloat_withNegativeValue_shouldRoundHalfUp() {
        assertThat(CurrencyUtils.roundFloat(-10.555f)).isEqualTo(-10.56f);
    }

    @Test
    void roundFloat_withWholeNumber_shouldReturnTwoDecimalPlaces() {
        Float result = CurrencyUtils.roundFloat(10f);
        assertThat(result).isEqualTo(10.00f);
    }
}