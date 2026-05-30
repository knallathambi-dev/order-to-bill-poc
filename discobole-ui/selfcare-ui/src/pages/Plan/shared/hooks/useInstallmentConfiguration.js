// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback, useEffect, useMemo, useState} from "react";
import {toast} from "react-toastify";
import {performActionAndGetCharacteristics} from "../services/productConfigurationService";
import {findMatchingPartnerCharges, getDefaultPartnerAndDuration, getSelectedPartner} from "../services/priceService";
import {UPFRONT_PAYMENT_VALUE} from "../../../../utlis/constants";

const deriveInitialTab = (installmentPeriodCharacteristic) => {
    const characteristicValues = installmentPeriodCharacteristic?.configurationCharacteristicValues;
    if (!characteristicValues) return "fullPrice";

    const selectedPeriod = characteristicValues.find(val => val.isSelected === true);

    if (!selectedPeriod?.characteristic?.value) return "fullPrice";
    if (selectedPeriod.characteristic.value === UPFRONT_PAYMENT_VALUE) return "fullPrice";

    return "installment";
};

const updateCharacteristicOnServer = async ({
                                                characteristicId,
                                                characteristicType,
                                                value,
                                                configurationId,
                                                configurationItemId,
                                                relatedParty,
                                                dispatch,
                                                tNotification,
                                                onConfigurationChange,
                                            }) => {
    const result = await performActionAndGetCharacteristics({
        configurationId,
        configurationItemId,
        characteristic: {
            id: characteristicId,
            value,
            type: characteristicType,
        },
        isSelected: true,
        relatedParty,
        actionType: "add",
        dispatch,
        tNotification,
        onConfigurationChange,
    });

    if (!result) {
        toast.error(tNotification("plan.updateCharacteristicFailed"));
        return false;
    }

    return true;
};

const findFirstDurationKey = (partnerCharges, partnerName) => {
    const firstChargeDuration =
        partnerCharges[0]?.productOfferingPrice?.applicationDuration?.amount;
    return firstChargeDuration ? `${partnerName}-${firstChargeDuration}` : null;
};

export const useInstallmentConfiguration = ({
                                                installmentPeriodCharacteristic,
                                                chargesByPartner,
                                                availablePartners = [],
                                                hasInstallments,
                                                partnerCharacteristic,
                                                configurationId,
                                                configurationItemId,
                                                relatedParty,
                                                dispatch,
                                                tNotification,
                                                onConfigurationChange,
                                                fetchAllPeriodsForPartner,
                                                periodOptions = [],
                                            }) => {
    const initialActiveTab = useMemo(
        () => deriveInitialTab(installmentPeriodCharacteristic),
        [installmentPeriodCharacteristic]
    );

    const [activeTab, setActiveTab] = useState(initialActiveTab);
    const [selectedPartner, setSelectedPartner] = useState(null);
    const [selectedDuration, setSelectedDuration] = useState({});
    const [isCompareModalOpen, setCompareModalOpen] = useState(false);

    useEffect(() => {
        setActiveTab(initialActiveTab);
    }, [initialActiveTab]);

    const serverUpdateParams = useMemo(() => ({
        configurationId,
        configurationItemId,
        relatedParty,
        dispatch,
        tNotification,
        onConfigurationChange,
    }), [configurationId, configurationItemId, relatedParty, dispatch, tNotification, onConfigurationChange]);

    const selectPartner = useCallback(
        async (partner, charges) => {
            setSelectedPartner(partner);

            const shouldUpdateServer =
                charges.length === 0 &&
                partnerCharacteristic &&
                configurationId &&
                configurationItemId;

            if (shouldUpdateServer) {
                await updateCharacteristicOnServer({
                    characteristicId: partnerCharacteristic.id,
                    characteristicType: partnerCharacteristic["@type"],
                    value: partner,
                    ...serverUpdateParams,
                });
            }
        },
        [partnerCharacteristic, configurationId, configurationItemId, serverUpdateParams]
    );

    const changeDuration = useCallback(
        async (partner, newDurationKey) => {
            setSelectedDuration(prev => ({...prev, [partner]: newDurationKey}));

            const durationAmount = newDurationKey.substring(newDurationKey.lastIndexOf("-") + 1);
            if (!installmentPeriodCharacteristic || !configurationId || !configurationItemId) return;

            const matchingPeriodValue = periodOptions.find(
                val => parseInt(val) === parseInt(durationAmount)
            );

            if (!matchingPeriodValue) return;

            await updateCharacteristicOnServer({
                characteristicId: installmentPeriodCharacteristic.id,
                characteristicType: installmentPeriodCharacteristic["@type"],
                value: matchingPeriodValue,
                ...serverUpdateParams,
            });
        },
        [installmentPeriodCharacteristic, configurationId, configurationItemId, periodOptions, serverUpdateParams]
    );

    const selectPeriodByValue = useCallback(
        async (periodValue) => {
            if (!installmentPeriodCharacteristic || !configurationId || !configurationItemId) return;

            await updateCharacteristicOnServer({
                characteristicId: installmentPeriodCharacteristic.id,
                characteristicType: installmentPeriodCharacteristic["@type"],
                value: periodValue,
                ...serverUpdateParams,
            });
        },
        [installmentPeriodCharacteristic, configurationId, configurationItemId, serverUpdateParams]
    );

    const selectFullPriceTab = useCallback(async () => {
        setActiveTab("fullPrice");

        const characteristicValues = installmentPeriodCharacteristic?.configurationCharacteristicValues;
        if (!characteristicValues) return;

        const hasUpfrontOption = characteristicValues.some(
            val => val.characteristic?.value === UPFRONT_PAYMENT_VALUE
        );

        if (hasUpfrontOption) {
            await selectPeriodByValue(UPFRONT_PAYMENT_VALUE);
        }
    }, [installmentPeriodCharacteristic, selectPeriodByValue]);

    const selectInstallmentTab = useCallback(async () => {
        setActiveTab("installment");

        const characteristicValues = installmentPeriodCharacteristic?.configurationCharacteristicValues;
        if (!characteristicValues) return;

        const firstInstallmentOption = characteristicValues.find(
            val => val.characteristic?.value && val.characteristic.value !== UPFRONT_PAYMENT_VALUE
        );

        if (firstInstallmentOption) {
            await selectPeriodByValue(firstInstallmentOption.characteristic.value);
        }
    }, [installmentPeriodCharacteristic, selectPeriodByValue]);

    const openCompareModal = useCallback(() => {
        setCompareModalOpen(true);

        if (!fetchAllPeriodsForPartner) return;

        const partnerToFetch = selectedPartner
            || (availablePartners.length > 0 ? availablePartners[0] : Object.keys(chargesByPartner || {})[0]);

        if (partnerToFetch) {
            fetchAllPeriodsForPartner(partnerToFetch);
        }
    }, [fetchAllPeriodsForPartner, selectedPartner, availablePartners, chargesByPartner]);

    useEffect(() => {
        if (selectedPartner) return;
        if (!hasInstallments && availablePartners.length === 0) return;

        const selectedPartnerFromCharacteristic = partnerCharacteristic
            ? getSelectedPartner(partnerCharacteristic)
            : null;

        const resolvedPartner =
            (selectedPartnerFromCharacteristic && chargesByPartner[selectedPartnerFromCharacteristic])
                ? selectedPartnerFromCharacteristic
                : availablePartners.length > 0
                    ? availablePartners[0]
                    : Object.keys(chargesByPartner)[0];

        if (!resolvedPartner) return;

        const partnerCharges = findMatchingPartnerCharges(resolvedPartner, chargesByPartner);
        selectPartner(resolvedPartner, partnerCharges);

        if (partnerCharges.length > 0) {
            const {durationKey} = getDefaultPartnerAndDuration(chargesByPartner, resolvedPartner);
            if (durationKey) {
                setSelectedDuration({[resolvedPartner]: durationKey});
            }
        }
    }, [hasInstallments, chargesByPartner, selectedPartner, availablePartners, partnerCharacteristic, selectPartner]);

    useEffect(() => {
        if (!hasInstallments || !selectedPartner) return;

        const partnerCharges = findMatchingPartnerCharges(selectedPartner, chargesByPartner);
        if (partnerCharges.length === 0) return;

        const currentDurationKey = selectedDuration[selectedPartner];
        const isCurrentKeyValid = currentDurationKey && partnerCharges.some(charge => {
            const chargeDuration = charge?.productOfferingPrice?.applicationDuration?.amount;
            return `${selectedPartner}-${chargeDuration}` === currentDurationKey;
        });

        if (currentDurationKey && isCurrentKeyValid) return;

        const fallbackKey = findFirstDurationKey(partnerCharges, selectedPartner);
        if (fallbackKey) {
            setSelectedDuration(prev => ({...prev, [selectedPartner]: fallbackKey}));
        }
    }, [hasInstallments, selectedPartner, chargesByPartner, selectedDuration]);

    return {
        activeTab,
        selectedPartner,
        selectedDuration,
        isCompareModalOpen,
        setCompareModalOpen,
        selectPartner,
        changeDuration,
        selectFullPriceTab,
        selectInstallmentTab,
        openCompareModal,
    };
};