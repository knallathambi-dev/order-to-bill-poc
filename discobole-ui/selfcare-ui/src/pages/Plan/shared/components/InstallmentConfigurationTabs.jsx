// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import InstallmentPaymentTabs from "./InstallmentPaymentTabs";
import {
    createFullPriceConfiguration,
    extractInstallmentCharges,
    extractInstallmentPeriodCharacteristic,
    extractPartnerCharacteristic,
    getAllAvailablePartners,
    groupInstallmentChargesByPartner,
} from "../services/priceService";
import {calculatePricingForConfigurationItems, getDiscounts} from "../services/utils/utils";
import {fetchConfiguration} from "../services/productConfigurationService";
import createProductConfigItemRequest from "../services/createProductConfigItemRequest";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {UPFRONT_PAYMENT_VALUE} from "../../../../utlis/constants";
import {useInstallmentConfiguration} from "../hooks/useInstallmentConfiguration";
import {useConfiguration} from "../context/ConfigurationContext";

const InstallmentConfigurationTabs = ({
                                          productConfiguration,
                                          configurationItemId,
                                          configurationId
                                      }) => {
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();
    const {relatedParty} = useSelector((state) => state.auth);

    const configurableCharacteristics = productConfiguration?.configurationCharacteristic || [];
    const configurationPrice = productConfiguration?.configurationPrice || [];
    const {onConfigurationChange} = useConfiguration();

    const installmentPeriodCharacteristic = useMemo(
        () => extractInstallmentPeriodCharacteristic(configurableCharacteristics),
        [configurableCharacteristics],
    );

    const partnerCharacteristic = useMemo(
        () => extractPartnerCharacteristic(configurableCharacteristics),
        [configurableCharacteristics],
    );

    const availablePartners = useMemo(
        () => getAllAvailablePartners(partnerCharacteristic),
        [partnerCharacteristic],
    );

    const periodOptions = useMemo(() => {
        if (!installmentPeriodCharacteristic) return [];
        return installmentPeriodCharacteristic.configurationCharacteristicValues
            .map((val) => val.characteristic.value)
            .filter((val) => val?.trim() && val !== UPFRONT_PAYMENT_VALUE);
    }, [installmentPeriodCharacteristic]);

    const [comparisonCharges, setComparisonCharges] = useState([]);

    const installmentCharges = useMemo(
        () => extractInstallmentCharges(configurationPrice),
        [configurationPrice],
    );

    const hasInstallments = installmentCharges.length > 0;

    const fullPriceConfigurationPrice = useMemo(
        () => createFullPriceConfiguration(configurationPrice, installmentCharges),
        [configurationPrice, installmentCharges],
    );

    const pricing = useMemo(() => {
        const priceConfig = fullPriceConfigurationPrice.length > 0
            ? {...productConfiguration, configurationPrice: fullPriceConfigurationPrice}
            : productConfiguration;

        return calculatePricingForConfigurationItems([{productConfiguration: priceConfig}]);
    }, [productConfiguration, fullPriceConfigurationPrice]);

    const discounts = useMemo(
        () => getDiscounts(fullPriceConfigurationPrice),
        [fullPriceConfigurationPrice],
    );

    const chargesByPartner = useMemo(
        () => groupInstallmentChargesByPartner(installmentCharges),
        [installmentCharges],
    );

    const fetchAllPeriodsForPartner = useCallback(async () => {
        if (!periodOptions?.length) return;

        const selectedPeriodValue = installmentPeriodCharacteristic?.configurationCharacteristicValues
            ?.find((val) => val.isSelected)?.characteristic?.value;

        const orderedPeriods = [...periodOptions];
        if (selectedPeriodValue && orderedPeriods.includes(selectedPeriodValue)) {
            orderedPeriods.sort((a, b) => (a === selectedPeriodValue ? 1 : b === selectedPeriodValue ? -1 : 0));
        }

        const collectedCharges = [];

        for (const periodValue of orderedPeriods) {
            try {
                const requestBody = createProductConfigItemRequest(
                    configurationId,
                    configurationItemId,
                    {
                        id: installmentPeriodCharacteristic.id,
                        value: periodValue,
                        type: installmentPeriodCharacteristic["@type"],
                    },
                    relatedParty,
                    true,
                    "add",
                );

                const targetItem = await fetchConfiguration(
                    requestBody, configurationItemId, dispatch, tNotification, {silent: true},
                );

                if (targetItem?.item.productConfiguration?.configurationPrice) {
                    collectedCharges.push(
                        ...extractInstallmentCharges(targetItem.item.productConfiguration.configurationPrice)
                    );
                }
            } catch (error) {
                console.error(`Error fetching period ${periodValue}:`, error);
            }
        }

        setComparisonCharges(collectedCharges);
    }, [configurationId, configurationItemId, installmentPeriodCharacteristic, periodOptions, relatedParty, dispatch, tNotification]);

    const {
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
    } = useInstallmentConfiguration({
        installmentPeriodCharacteristic,
        chargesByPartner,
        availablePartners,
        hasInstallments,
        partnerCharacteristic,
        configurationId,
        configurationItemId,
        relatedParty,
        dispatch,
        tNotification,
        onConfigurationChange,
        fetchAllPeriodsForPartner,
        periodOptions,
    });

    const handleOpenCompareModal = useCallback(() => {
        setComparisonCharges([]);
        openCompareModal();
    }, [openCompareModal]);

    return (
        <InstallmentPaymentTabs
            hasInstallments={hasInstallments}
            chargesByPartner={chargesByPartner}
            comparisonCharges={comparisonCharges}
            periodOptions={periodOptions}
            availablePartners={availablePartners}
            pricing={pricing}
            discounts={discounts}
            t={t}
            activeTab={activeTab}
            selectedPartner={selectedPartner}
            selectedDuration={selectedDuration}
            isCompareModalOpen={isCompareModalOpen}
            setCompareModalOpen={setCompareModalOpen}
            onPartnerSelect={selectPartner}
            onDurationChange={changeDuration}
            onFullPriceTabClick={selectFullPriceTab}
            onInstallmentTabClick={selectInstallmentTab}
            onOpenCompareModal={handleOpenCompareModal}
            showPaymentLabel
        />
    );
};

export default InstallmentConfigurationTabs;