// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    INSTALLMENT_CHARGE_TYPE,
    INSTALLMENT_PERIOD_CHARACTERISTIC_NAME,
    PARTNER_CHARACTERISTIC_NAME,
    PRICE_TYPES,
} from "../../../../utlis/constants";

export const extractInstallmentCharges = (configurationPrice) => {
    if (!Array.isArray(configurationPrice)) return [];
    return configurationPrice.filter(
        (price) => price?.productOfferingPrice?.["@type"] === INSTALLMENT_CHARGE_TYPE
    );
};

export const createFullPriceConfiguration = (configurationPrice, installmentCharges) => {
    if (!Array.isArray(configurationPrice)) return [];

    const nonInstallment = configurationPrice.filter(
        (price) => price?.productOfferingPrice?.["@type"] !== INSTALLMENT_CHARGE_TYPE
    );

    if (nonInstallment.length === 0 && installmentCharges.length > 0) {
        return installmentCharges.map((charge) => ({
            ...charge,
            priceType: PRICE_TYPES.NON_RECURRING_CHARGE,
            productOfferingPrice: {
                ...charge.productOfferingPrice,
                applicationDuration: undefined,
                interestRate: undefined,
                downPayment: undefined,
            },
        }));
    }

    return nonInstallment;
};

export const groupInstallmentChargesByPartner = (installmentCharges) => {
    if (!installmentCharges?.length) return {};

    const grouped = {};
    for (const charge of installmentCharges) {
        const partner = charge?.productOfferingPrice?.partner || "Unknown";
        (grouped[partner] ??= []).push(charge);
    }
    return grouped;
};

export const getDefaultPartnerAndDuration = (installmentByPartner, selectedPartner = null) => {
    const partners = Object.keys(installmentByPartner || {});
    if (partners.length === 0) return {partner: null, durationKey: null};

    const partner = (selectedPartner && installmentByPartner[selectedPartner])
        ? selectedPartner
        : partners[0];

    const charges = installmentByPartner[partner];
    if (!charges?.length) return {partner: null, durationKey: null};

    const duration = charges[0]?.productOfferingPrice?.applicationDuration;
    if (!duration) return {partner, durationKey: null};

    return {partner, durationKey: `${partner}-${duration.amount}`};
};

export const computeInstallmentPricing = (charge) => {
    if (!charge) return null;

    const pop = charge.productOfferingPrice;
    const price = charge.price;

    const currency = price?.taxIncludedAmount?.unit || price?.dutyFreeAmount?.unit;
    const baseAmount = price?.taxIncludedAmount?.value;
    const interestRate = pop?.interestRate;
    const downPayment = pop?.downPayment;
    const duration = pop?.applicationDuration?.amount;
    const durationUnit = pop?.applicationDuration?.units;

    const totalPayment = baseAmount * (1 + interestRate / 100);
    const financedAmount = totalPayment - downPayment;
    const monthlyInstallment = duration > 0 ? financedAmount / duration : 0;
    const periodSuffix = durationUnit ? `/${durationUnit}` : '';

    return {
        currency,
        downPayment,
        monthlyInstallment,
        totalPayment,
        duration,
        durationUnit,
        periodSuffix,
        interestRate,
    };
};

const safeFindCharacteristic = (characteristics, name) => {
    if (!Array.isArray(characteristics)) return null;
    return characteristics.find((char) => char.name === name) || null;
};

export const filterDisplayableCharacteristics = (characteristics) => {
    if (!Array.isArray(characteristics)) return [];
    return characteristics.filter(
        (char) => char.name !== INSTALLMENT_PERIOD_CHARACTERISTIC_NAME &&
            char.name !== PARTNER_CHARACTERISTIC_NAME
    );
};

export const extractInstallmentPeriodCharacteristic = (characteristics) =>
    safeFindCharacteristic(characteristics, INSTALLMENT_PERIOD_CHARACTERISTIC_NAME);

export const extractPartnerCharacteristic = (characteristics) =>
    safeFindCharacteristic(characteristics, PARTNER_CHARACTERISTIC_NAME);

export const getAllAvailablePartners = (partnerCharacteristic) => {
    if (!partnerCharacteristic?.configurationCharacteristicValues) return [];
    return partnerCharacteristic.configurationCharacteristicValues.map(
        (val) => val.characteristic.value
    );
};

export const getSelectedPartner = (partnerCharacteristic) => {
    if (!partnerCharacteristic?.configurationCharacteristicValues) return null;
    const selected = partnerCharacteristic.configurationCharacteristicValues.find(
        (val) => val.isSelected === true
    );
    return selected?.characteristic?.value || null;
};

export const findMatchingPartnerCharges = (partnerName, installmentByPartner) => {
    if (!partnerName || !installmentByPartner) return [];
    if (installmentByPartner[partnerName]) return installmentByPartner[partnerName];

    const normalize = (s) => s.toLowerCase().replace(/\s+/g, "");
    const needle = normalize(partnerName);

    const matchingKey = Object.keys(installmentByPartner).find((key) => {
        const k = normalize(key);
        return needle.includes(k) || k.includes(needle);
    });

    return matchingKey ? installmentByPartner[matchingKey] : [];
};