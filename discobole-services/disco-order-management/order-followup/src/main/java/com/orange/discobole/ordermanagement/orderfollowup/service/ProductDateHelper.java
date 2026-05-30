// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service;

import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants;
import com.orange.discobole.productinventory.dto.v1.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants.*;

@Component
@Slf4j
public class ProductDateHelper {
    private static final Map<String, ChronoUnit> UNIT_MAP = Map.of(
            "day", ChronoUnit.DAYS,
            "days", ChronoUnit.DAYS,
            "month", ChronoUnit.MONTHS,
            "months", ChronoUnit.MONTHS,
            "year", ChronoUnit.YEARS,
            "years", ChronoUnit.YEARS
    );

    /**
     * Updates product price dates for qualifying products
     *
     * @param product        the product to update
     * @param productOrderId the product order ID to match
     */
    public List<PatchDTO> updateProductPriceDate(Product product, String productOrderId) {
        List<PatchDTO> patchList = new ArrayList<>();
        if (!shouldUpdatePriceDate(product, productOrderId)) {
            log.debug("Skipping product price date update for productId={} and orderId={}", product.getId(), productOrderId);
            return Collections.emptyList();
        }

        OffsetDateTime startDate = product.getStartDate();
        Optional.ofNullable(product.getProductPrice())
                .ifPresent(prices -> updateAllProductPrices(product, prices, startDate, patchList));

        log.debug("Generated {} price date patches for productId={} and orderId={}", patchList.size(), product.getId(), productOrderId);
        return patchList;
    }

    public List<PatchDTO> updateProductTermDate(Product product, String productOrderId) {
        List<PatchDTO> patchList = new ArrayList<>();
        if (!shouldUpdateProductTerm(product, productOrderId)) {
            log.debug("Skipping product term date update for productId={} and orderId={}", product.getId(), productOrderId);
            return Collections.emptyList();
        }
        product.getProductTerm().forEach(
                productTerm -> addPatchForProductTermDate(patchList, productTerm, product, product.getProductTerm().indexOf(productTerm)));
        log.debug("Generated {} product term patches for productId={} and orderId={}", patchList.size(), product.getId(), productOrderId);
        return patchList;
    }

    private void addPatchForProductTermDate(List<PatchDTO> patchList, ProductTerm productTerm, Product product, int productTermIndex) {
        TimePeriod validFor = createTimePeriodForProductTerm(product.getStartDate(), productTerm.getDuration());
        String path = PRODUCT_INVENTORY_URI + product.getId() + PRODUCT_TERM + productTermIndex + VALID_FOR_URI;
        patchList.add(createPatchForAdd(path, validFor));

    }

    /**
     * Determines if product price date should be updated based on business rules
     */
    private boolean shouldUpdatePriceDate(Product product, String productOrderId) {
        return isAddMigrateItem(product, productOrderId)
                && isActiveOrSold(product)
                && (hasRecurringCharge(product) || hasInstallmentCharge(product))
                && product.getStartDate() != null;
    }

    private boolean hasInstallmentCharge(Product product) {
        return Objects.nonNull(product.getProductPrice())
                && product.getProductPrice().stream().anyMatch(InstallmentCharge.class::isInstance);
    }

    private boolean shouldUpdateProductTerm(Product product, String productOrderId) {
        return isAddMigrateItem(product, productOrderId)
                && isActiveOrSold(product)
                && product.getStartDate() != null
                && !CollectionUtils.isEmpty(product.getProductTerm());
    }

    /**
     * Checks if product status is ACTIVE or SOLD
     */
    private boolean isActiveOrSold(Product product) {
        ProductStatusType status = product.getStatus();
        return ProductStatusType.ACTIVE.equals(status) || ProductStatusType.SOLD.equals(status);
    }

    /**
     * Updates all product prices with the given start date
     */
    private void updateAllProductPrices(Product product, List<ProductPrice> productPrices, OffsetDateTime startDate, List<PatchDTO> patchList) {
        for (int productPriceIndex = 0; productPriceIndex < productPrices.size(); productPriceIndex++) {
            ProductPrice price = productPrices.get(productPriceIndex);
            updateProductPrice(price, product.getId(), productPriceIndex, startDate, patchList);
            updateProductPriceAlteration(price, product.getId(), productPriceIndex, startDate, patchList);
        }
    }

    private void updateProductPriceAlteration(ProductPrice price, String productId, int productPriceIndex, OffsetDateTime startDate, List<PatchDTO> patchList) {
        List<PriceAlteration> alterations = price.getProductPriceAlteration();
        if (CollectionUtils.isEmpty(alterations)) {
            return;
        }

        for (int alterationIndex = 0; alterationIndex < alterations.size(); alterationIndex++) {
            PriceAlteration alteration = alterations.get(alterationIndex);

            if (isDiscountAlteration(price, alteration)) {
                handleDiscountAlteration(alteration, startDate, price, productId, productPriceIndex, alterationIndex, patchList);
            } else {
                handleRegularAlteration(alteration, startDate, productId, productPriceIndex, alterationIndex, patchList);
            }
        }
    }

    private void handleDiscountAlteration(PriceAlteration alteration, OffsetDateTime startDate, ProductPrice price,
                                          String productId, int productPriceIndex, int alterationIndex, List<PatchDTO> patchList) {
        String units = price.getRecurringChargePeriod().getUnits();

        if (FollowUpConstants.RECURRING_DISCOUNT.equals(alteration.getPriceType())) {
            updatePriceDateForRecurringCharge(alteration, startDate, units,
                    PriceAlteration::getApplicationOffset,
                    PriceAlteration::getApplicationDuration,
                    PriceAlteration::setValidFor);
        } else if (FollowUpConstants.NON_RECURRING_DISCOUNT.equals(alteration.getPriceType())) {
            updatePriceDateForNonRecurringCharge(alteration, startDate, units,
                    PriceAlteration::getApplicationOffset,
                    PriceAlteration::setValidFor);
        }

        createPatchForProductPriceAlterationDate(productId, productPriceIndex, alterationIndex, patchList, alteration);
    }

    private void handleRegularAlteration(PriceAlteration alteration, OffsetDateTime startDate,
                                         String productId, int productPriceIndex, int alterationIndex, List<PatchDTO> patchList) {
        updatePriceDate(alteration, startDate,
                PriceAlteration::getRecurringChargePeriod,
                PriceAlteration::getApplicationDuration,
                PriceAlteration::setValidFor);
        createPatchForProductPriceAlterationDate(productId, productPriceIndex, alterationIndex, patchList, alteration);
    }

    private boolean isDiscountAlteration(ProductPrice price, PriceAlteration alteration) {
        return FollowUpConstants.DISCOUNT_PRICE_ALTERATION.equals(alteration.getAtType())
                && alteration.getApplicationOffset() != null
                && price.getRecurringChargePeriod() != null
                && price.getRecurringChargePeriod().getUnits() != null;
    }

    private void updateProductPrice(ProductPrice price, String productId, int productPriceIndex, OffsetDateTime startDate, List<PatchDTO> patchList) {
        if (price instanceof InstallmentCharge) {
            updatePriceDateForInstallment(price, startDate,
                    ProductPrice::getApplicationDuration,
                    ProductPrice::setValidFor);
        } else {
            updatePriceDate(price, startDate,
                    ProductPrice::getRecurringChargePeriod,
                    ProductPrice::getApplicationDuration,
                    ProductPrice::setValidFor);
        }
        createPatchForProductPriceDate(productId, patchList, price, productPriceIndex);
    }

    private void createPatchForProductPriceDate(String productId, List<PatchDTO> patchList, ProductPrice price, int productPriceIndex) {
        if (price.getValidFor() != null) {
            String path = PRODUCT_INVENTORY_URI + productId + PRODUCT_PRICE_URI + productPriceIndex + VALID_FOR_URI;
            patchList.add(createPatchForAdd(path, price.getValidFor()));
        }
    }

    private PatchDTO createPatchForAdd(String path, Object value) {
        return PatchDTO.builder()
                .op(PatchOperationType.ADD)
                .path(path)
                .value(value)
                .build();
    }

    private <T> void updatePriceDateForRecurringCharge(T alterationObject,
                                                       OffsetDateTime startDate,
                                                       String units,
                                                       ToIntFunction<T> applicationOffsetGetter,
                                                       Function<T, Quantity> applicationDurationGetter,
                                                       BiConsumer<T, TimePeriod> validForSetter) {

        Integer applicationOffset = applicationOffsetGetter.applyAsInt(alterationObject);
        OffsetDateTime newStartDate = calculateDate(startDate, applicationOffset, units);
        Quantity applicationDuration = applicationDurationGetter.apply(alterationObject);
        TimePeriod timePeriod = createTimePeriod(newStartDate, applicationDuration);
        validForSetter.accept(alterationObject, timePeriod);
    }

    private <T> void updatePriceDateForNonRecurringCharge(T alterationObject,
                                                          OffsetDateTime startDate,
                                                          String units,
                                                          ToIntFunction<T> applicationOffsetGetter,
                                                          BiConsumer<T, TimePeriod> validForSetter) {

        Integer applicationOffset = applicationOffsetGetter.applyAsInt(alterationObject);
        OffsetDateTime newStartDate = calculateDate(startDate, applicationOffset, units);
        OffsetDateTime endDate = calculateDate(newStartDate, 1, units);
        TimePeriod timePeriod = TimePeriod.builder()
                .startDateTime(newStartDate)
                .endDateTime(endDate)
                .build();
        validForSetter.accept(alterationObject, timePeriod);
    }

    private void createPatchForProductPriceAlterationDate(String productId, int productPriceIndex, int alterationIndex, List<PatchDTO> patchList, PriceAlteration alteration) {
        if (alteration.getValidFor() != null) {
            String path = PRODUCT_INVENTORY_URI + productId + PRODUCT_PRICE_URI + productPriceIndex + PRODUCT_PRICE_ALTERATION_URI + alterationIndex + VALID_FOR_URI;
            patchList.add(createPatchForAdd(path, alteration.getValidFor()));
        }
    }

    /**
     * Generic method to update price dates for any price object type
     *
     * @param <T>                         the type of price object (ProductPrice or PriceAlteration)
     * @param priceObject                 the object to update
     * @param startDate                   the start date to use
     * @param recurringChargePeriodGetter function to get recurring charge period
     * @param applicationDurationGetter   function to get application duration
     * @param validForSetter              function to set the valid for period
     */
    private <T> void updatePriceDate(T priceObject,
                                     OffsetDateTime startDate,
                                     Function<T, Object> recurringChargePeriodGetter,
                                     Function<T, Quantity> applicationDurationGetter,
                                     BiConsumer<T, TimePeriod> validForSetter) {

        if (recurringChargePeriodGetter.apply(priceObject) == null) {
            return;
        }

        Quantity applicationDuration = applicationDurationGetter.apply(priceObject);
        TimePeriod timePeriod = createTimePeriod(startDate, applicationDuration);
        validForSetter.accept(priceObject, timePeriod);
    }

    /**
     * Generic method to update price dates for any price object type
     *
     * @param <T>                       the type of price object (ProductPrice or PriceAlteration)
     * @param priceObject               the object to update
     * @param startDate                 the start date to use
     * @param applicationDurationGetter function to get application duration
     * @param validForSetter            function to set the valid for period
     */
    private <T> void updatePriceDateForInstallment(T priceObject,
                                                   OffsetDateTime startDate,
                                                   Function<T, Quantity> applicationDurationGetter,
                                                   BiConsumer<T, TimePeriod> validForSetter) {

        if (applicationDurationGetter.apply(priceObject) == null) {
            return;
        }

        Quantity applicationDuration = applicationDurationGetter.apply(priceObject);
        TimePeriod timePeriod = createTimePeriod(startDate, applicationDuration);
        validForSetter.accept(priceObject, timePeriod);
    }

    /**
     * Creates a time period based on start date and optional duration
     *
     * @param startDate           the start date
     * @param applicationDuration optional duration specification
     * @return TimePeriod with start and optionally end date
     */
    private TimePeriod createTimePeriod(OffsetDateTime startDate, Quantity applicationDuration) {
        TimePeriod.TimePeriodBuilder<?, ?> builder = TimePeriod.builder()
                .startDateTime(startDate);

        Optional.ofNullable(applicationDuration)
                .filter(measuredValue -> hasValidAmountAndUnit(measuredValue.getAmount(), measuredValue.getUnits()))
                .ifPresent(duration -> {
                    OffsetDateTime endDate = calculateDate(startDate, duration.getAmount().intValue(), duration.getUnits());
                    builder.endDateTime(endDate);
                });

        return builder.build();
    }

    private TimePeriod createTimePeriodForProductTerm(OffsetDateTime startDate, Duration quantity) {
        TimePeriod.TimePeriodBuilder<?, ?> builder = TimePeriod.builder().startDateTime(startDate);

        Optional.ofNullable(quantity)
                .filter(quantityValue -> hasValidAmountAndUnit(quantityValue.getAmount(), quantityValue.getUnits()))
                .ifPresent(quantityItem -> {
                    OffsetDateTime endDate = calculateDate(startDate, quantityItem.getAmount(), quantityItem.getUnits());
                    builder.endDateTime(endDate);
                });

        return builder.build();
    }

    /**
     * Validates that measured value has both amount and unit
     */
    private boolean hasValidAmountAndUnit(Float amount, String units) {
        return amount != null && units != null;
    }

    private boolean hasValidAmountAndUnit(Integer amount, String units) {
        return amount != null && units != null;
    }

    /**
     * Calculates date based on start date and duration
     */
    private OffsetDateTime calculateDate(OffsetDateTime startDate, Integer amount, String units) {
        ChronoUnit chronoUnit = getDateUnit(units);
        return startDate.plus(amount.longValue(), chronoUnit);
    }


    /**
     * Maps string unit to ChronoUnit with validation
     *
     * @param unit the unit string (case-insensitive)
     * @return corresponding ChronoUnit
     * @throws IllegalArgumentException if unit is not supported
     */
    private ChronoUnit getDateUnit(String unit) {
        return Optional.ofNullable(UNIT_MAP.get(unit.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported unit: %s. Supported units: %s",
                                unit, UNIT_MAP.keySet())));
    }

    /**
     * Checks if product has any recurring charges in prices or alterations
     *
     * @param product the product to check
     * @return true if product has recurring charges
     */
    private boolean hasRecurringCharge(Product product) {
        return Optional.ofNullable(product.getProductPrice())
                .map(this::hasAnyRecurringCharge)
                .orElse(false);
    }

    /**
     * Checks if any product price has recurring charges
     */
    private boolean hasAnyRecurringCharge(List<ProductPrice> productPrices) {
        return productPrices.stream().anyMatch(this::hasRecurringChargeInPrice);
    }

    /**
     * Checks if a single product price has recurring charges
     */
    private boolean hasRecurringChargeInPrice(ProductPrice productPrice) {
        return productPrice.getRecurringChargePeriod() != null ||
                hasRecurringChargeInAlterations(productPrice.getProductPriceAlteration());
    }

    /**
     * Checks if any price alteration has recurring charges
     */
    private boolean hasRecurringChargeInAlterations(List<PriceAlteration> alterations) {
        return Optional.ofNullable(alterations)
                .map(list -> list.stream().anyMatch(alt -> alt.getRecurringChargePeriod() != null))
                .orElse(false);
    }

    /**
     * Checks if product is an ADD or MIGRATE item for the given order
     *
     * @param product        the product to check
     * @param productOrderId the order ID to match
     * @return true if product is an ADD item for the specified order
     */
    private boolean isAddMigrateItem(Product product, String productOrderId) {
        return Optional.ofNullable(product.getProductOrderItem())
                .map(items -> items.stream().anyMatch(createAddMigrateItemPredicate(productOrderId)))
                .orElse(false);
    }

    /**
     * Creates a predicate to check if an order item is an ADD or MIGRATE action for the given order ID
     */
    private Predicate<RelatedProductOrderItem> createAddMigrateItemPredicate(String productOrderId) {
        return item -> productOrderId.equals(item.getProductOrderId()) &&
                (ADD.equals(item.getOrderItemAction()) || MIGRATE.equals(item.getOrderItemAction()));
    }
}
