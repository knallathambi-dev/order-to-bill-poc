// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo, useState} from "react";
import {Modal} from "../../../../components";
import AddressModal from "../../SetUpPlan/components/Modals/AddressModal";
import {handleAddressConfiguration} from "../../../Eligibility/services/eligibilityService";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {
    ADDRESS_CHARACTERISTIC_TYPE,
    COLOR_KEYWORDS,
    INSTALLMENT_PERIOD_CHARACTERISTIC_NAME,
    PARTNER_CHARACTERISTIC_NAME
} from "../../../../utlis/constants";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {hasValidUnitOfMeasure} from "../services/utils/utils";
import InstallmentConfigurationTabs from "./InstallmentConfigurationTabs";
import {useConfiguration} from "../context/ConfigurationContext";

const getUniqueValues = (values) => {
    return [...new Map(
        (values || []).map((item) => [item.characteristic.value, item])
    ).values()];
};

const getSortedValues = (values) => {
    return [...values].sort((a, b) =>
        parseFloat(a?.characteristic?.value || 0) - parseFloat(b?.characteristic?.value || 0)
    );
};

const useAddressModal = () => {
    const {configuration, relatedParty, dispatch, tNotification} = useConfiguration();
    const [isModalOpen, setModalOpen] = useState(false);
    const [editedAddress, setEditedAddress] = useState();
    const [isAddressValid, setIsAddressValid] = useState(false);

    const handleOpenModal = useCallback(() => setModalOpen(true), []);

    const handleCloseModal = useCallback(() => {
        setModalOpen(false);
        setIsAddressValid(false);
    }, []);

    const handleAddressSave = useCallback(async () => {
        if (!isAddressValid) return;

        dispatch(toggleLoading(true));

        try {
            await handleAddressConfiguration(
                editedAddress,
                configuration,
                relatedParty,
                dispatch,
                tNotification
            );

            dispatch(toggleLoading(false));
            handleCloseModal();
        } catch (error) {
            dispatch(toggleLoading(false));
        }
    }, [isAddressValid, editedAddress, configuration, relatedParty, dispatch, handleCloseModal, tNotification]);

    return {
        isModalOpen,
        editedAddress,
        isAddressValid,
        setEditedAddress,
        setIsAddressValid,
        handleOpenModal,
        handleCloseModal,
        handleAddressSave,
    };
};

const AddressCharacteristic = ({
                                   characteristic,
                                   uniqueKeyPrefix,
                                   isDisabled,
                                   addressModal,
                                   t,
                               }) => {
    const addressCharacteristic = characteristic.configurationCharacteristicValues.find(
        (item) => item.isSelected === true
    )?.characteristic;

    if (!addressCharacteristic) return null;

    const addressParts = [
        addressCharacteristic.subUnitNumber?.trim() && `${addressCharacteristic.subUnitNumber} `,
        addressCharacteristic.streetName,
        addressCharacteristic.city,
        `${addressCharacteristic.country} - ${addressCharacteristic.postcode}`
    ].filter(Boolean).join(', ');

    return (
        <div className="card mb-2" key={uniqueKeyPrefix}>
            <div className="card-body py-1">
                <div className="d-flex justify-content-between align-items-center">
                    <p className="mb-0">
                        <strong>{t("forms.labels.address")}:</strong> {addressParts}
                    </p>
                    <button
                        type="button"
                        className="btn btn-link px-0"
                        onClick={addressModal.handleOpenModal}
                        disabled={isDisabled}
                    >
                        {t("actions.editAddress")}
                    </button>
                </div>
            </div>
            {addressModal.isModalOpen && (
                <Modal
                    show={addressModal.isModalOpen}
                    title={t("plan.editAddress")}
                    body={
                        <AddressModal
                            address={addressCharacteristic}
                            onAddressChange={addressModal.setEditedAddress}
                            onValidationChange={addressModal.setIsAddressValid}
                        />
                    }
                    onSave={addressModal.handleAddressSave}
                    onClose={addressModal.handleCloseModal}
                    saveDisabled={!addressModal.isAddressValid}
                />
            )}
        </div>
    );
};

const RangeTick = ({value, index, totalValues, isCurrentValue, showLabel, uniqueKeyPrefix, t}) => {
    const leftPercent = index * (100 / (totalValues - 1));

    return (
        <React.Fragment key={`tick-${uniqueKeyPrefix}-${index}`}>
            {!isCurrentValue && (
                <div
                    style={{
                        position: "absolute",
                        left: `${leftPercent}%`,
                        textAlign: "center",
                    }}
                >
          <span
              className="tick"
              title={`${value.characteristic.value} ${value.characteristic.unitOfMeasure}`}
          />
                </div>
            )}
            {showLabel && (
                <div
                    style={{
                        position: "absolute",
                        left: `${leftPercent}%`,
                        textAlign: "center",
                    }}
                >
                    <div
                        className="d-flex flex-column align-items-center mt-5"
                        style={{transform: "translateX(-50%)"}}
                    >
                        <div className="current-value-text">
                            {t("plan.currentValue")}
                        </div>
                    </div>
                </div>
            )}
        </React.Fragment>
    );
};

const RangeCharacteristic = ({
                                 sortedValues,
                                 selectedValue,
                                 uniqueKeyPrefix,
                                 name,
                                 isDisabled,
                                 configurationItemId,
                                 actionType,
                                 handleRangeChange,
                                 handleRangeChangeEnd,
                                 isProductCharacteristicValuePersisted,
                                 shouldShowCurrentValue,
                                 t,
                             }) => {
    const divisionValues = sortedValues.map((value) => parseFloat(value.characteristic.value));
    const selectedIndex = divisionValues.indexOf(parseFloat(selectedValue));
    const rangeValue = selectedIndex * (100 / (divisionValues.length - 1)) || 0;

    return (
        <div className="row" key={`${uniqueKeyPrefix}-range`}>
            <div className="col-md-1">
                <label className="form-label me-2 fw-medium range-label">{name}</label>
            </div>
            <div className="col-md-11">
                <div className="items-range pe-3">
                    <div className="range-container">
                        <input
                            type="range"
                            id={`selectRange-${uniqueKeyPrefix}`}
                            list="values"
                            className="form-range custom-range"
                            min={0}
                            max={100}
                            value={rangeValue}
                            onChange={(e) => handleRangeChange(e, configurationItemId, sortedValues, name)}
                            onMouseUp={() => handleRangeChangeEnd(
                                configurationItemId,
                                sortedValues,
                                sortedValues[0]?.characteristic,
                                actionType
                            )}
                            onTouchEnd={() => handleRangeChangeEnd(
                                configurationItemId,
                                sortedValues,
                                sortedValues[0]?.characteristic,
                                actionType
                            )}
                            disabled={isDisabled}
                        />
                        <datalist id="values">
                            {sortedValues.map((value, index) => (
                                <option
                                    key={index}
                                    value={index * (100 / (sortedValues.length - 1))}
                                    label={`${value.characteristic.value} ${value.characteristic.unitOfMeasure}`}
                                    disabled={value.isSelectable === false && value.isSelected === false}
                                />
                            ))}
                        </datalist>

                        <div className="range-ticks">
                            {sortedValues.map((value, index) => {
                                const isCurrentValue = value.characteristic.value === selectedValue;
                                const showLabel = shouldShowCurrentValue && isProductCharacteristicValuePersisted(value, name);

                                return (
                                    <RangeTick
                                        key={`tick-${uniqueKeyPrefix}-${index}`}
                                        value={value}
                                        index={index}
                                        totalValues={sortedValues.length}
                                        isCurrentValue={isCurrentValue}
                                        showLabel={showLabel}
                                        uniqueKeyPrefix={uniqueKeyPrefix}
                                        t={t}
                                    />
                                );
                            })}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const RadioOption = ({
                         value,
                         idx,
                         uniqueKeyPrefix,
                         selectedValue,
                         isColorCharacteristic,
                         showCurrentValueLabel,
                         isDisabled,
                         configurationItemId,
                         actionType,
                         handleCharacteristicUpdate,
                         t,
                     }) => {
    const actualValue = value.characteristic.value;
    const isCurrentValue = actualValue === selectedValue;

    return (
        <div
            key={`${uniqueKeyPrefix}-option-${idx}`}
            className="d-flex flex-column align-items-center text-center"
        >
            <div className="d-flex align-items-center">
                <input
                    type="radio"
                    className="btn-check"
                    name={`${uniqueKeyPrefix}-radio-group`}
                    id={`${uniqueKeyPrefix}-option-${idx}`}
                    value={actualValue}
                    checked={isCurrentValue}
                    onChange={async () => {
                        if (!isDisabled) {
                            await handleCharacteristicUpdate(
                                configurationItemId,
                                value.characteristic,
                                actualValue,
                                actionType
                            );
                        }
                    }}
                    disabled={!value.isSelectable || isDisabled}
                />
                <label
                    className={`btn btn-outline-secondary ${
                        isColorCharacteristic
                            ? `rounded-pill color-label ${actualValue.toLowerCase()} border-thick`
                            : "size"
                    } ${
                        !isColorCharacteristic && isCurrentValue
                            ? "bg-black text-white"
                            : ""
                    }`}
                    htmlFor={`${uniqueKeyPrefix}-option-${idx}`}
                >
                    {actualValue} {value.characteristic.unitOfMeasure}
                </label>
            </div>
            {showCurrentValueLabel && (
                <div className="current-value-text text-center mt-1">
                    {t("plan.currentValue")}
                </div>
            )}
        </div>
    );
};

const RadioCharacteristic = ({
                                 sortedValues,
                                 selectedValue,
                                 uniqueKeyPrefix,
                                 name,
                                 isDisabled,
                                 configurationItemId,
                                 actionType,
                                 handleCharacteristicUpdate,
                                 isProductCharacteristicValuePersisted,
                                 shouldShowCurrentValue,
                                 t,
                             }) => {
    const isColorCharacteristic = COLOR_KEYWORDS.includes(name.toLowerCase());

    return (
        <div
            key={`${uniqueKeyPrefix}-radio`}
            className={isColorCharacteristic ? "color-wrapper" : "size-wrapper"}
        >
            <div className="d-flex align-items-center gap-2">
                <label className="form-label me-2 fw-medium range-label">{name}</label>
                <div className="btn-group d-flex flex-wrap" role="group">
                    {sortedValues.map((value, idx) => {
                        const showCurrentValueLabel = shouldShowCurrentValue &&
                            isProductCharacteristicValuePersisted(value, name);

                        return (
                            <RadioOption
                                key={`${uniqueKeyPrefix}-option-${idx}`}
                                value={value}
                                idx={idx}
                                uniqueKeyPrefix={uniqueKeyPrefix}
                                selectedValue={selectedValue}
                                isColorCharacteristic={isColorCharacteristic}
                                showCurrentValueLabel={showCurrentValueLabel}
                                isDisabled={isDisabled}
                                configurationItemId={configurationItemId}
                                actionType={actionType}
                                handleCharacteristicUpdate={handleCharacteristicUpdate}
                                t={t}
                            />
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

const InstallmentPricing = ({
                                productConfiguration,
                                configurationItemId
                            }) => {
    const {configuration} = useConfiguration();

    const hasInstallmentPeriodCharacteristic = productConfiguration?.configurationCharacteristic?.some(
        (char) => char.name === INSTALLMENT_PERIOD_CHARACTERISTIC_NAME
    );

    if (!hasInstallmentPeriodCharacteristic) {
        return null;
    }

    return (
        <div className="mb-3">
            <InstallmentConfigurationTabs
                productConfiguration={productConfiguration}
                configurationItemId={configurationItemId}
                configurationId={configuration.id}
            />
        </div>
    );
};

export const RenderCharacteristics = ({
                                          productConfiguration,
                                          configurationItemId,
                                          actionType,
                                          checkAncestorsSelection,
                                          handleRangeChange,
                                          handleRangeChangeEnd,
                                          handleCharacteristicUpdate,
                                      }) => {
    const {configuration} = useConfiguration();
    const {t} = useTranslations();

    const addressModal = useAddressModal();

    const shouldShowCurrentValue = !!actionType;
    const shouldDisableInputs = !actionType;

    const getSelectedValue = useCallback((sortedValues) =>
            sortedValues.find(item => item?.isSelected)?.characteristic?.value ||
            sortedValues.find(item => !item?.isSelected && !item?.isSelectable)?.characteristic?.value ||
            null
        , []);

    const isProductCharacteristicValuePersisted = useCallback((value, charName) => {
        const persistedValue = productConfiguration?.product?.productCharacteristic?.find(
            (pChar) => pChar.name === charName
        );

        if (!persistedValue) return false;

        const persistedVal = persistedValue.value.value !== undefined
            ? persistedValue.value.value
            : persistedValue.value;

        return persistedVal === value.characteristic.value &&
            !value.isSelectable &&
            !value.isSelected;
    }, [productConfiguration]);

    const hasInstallmentCharges = useMemo(() => {
        return productConfiguration?.configurationCharacteristic?.some(
            (char) => char.name === INSTALLMENT_PERIOD_CHARACTERISTIC_NAME
        );
    }, [productConfiguration?.configurationCharacteristic]);

    const renderCharacteristic = useCallback((characteristic) => {
        if (!characteristic?.isConfigurable) return null;

        const {name, configurationCharacteristicValues} = characteristic;

        if ((name === PARTNER_CHARACTERISTIC_NAME || name === INSTALLMENT_PERIOD_CHARACTERISTIC_NAME) && hasInstallmentCharges) {
            return null;
        }

        const uniqueValues = getUniqueValues(configurationCharacteristicValues);
        const sortedValues = getSortedValues(uniqueValues);
        const selectedValue = getSelectedValue(sortedValues);
        const hasValidUnit = hasValidUnitOfMeasure(configurationCharacteristicValues);
        const isDisabled = !checkAncestorsSelection(configurationItemId, configuration.computedProductConfigurationItem) || shouldDisableInputs;
        const uniqueKeyPrefix = `${configurationItemId}-${name}`;

        if (configurationCharacteristicValues.length <= 1 && !hasValidUnit) {
            return null;
        }

        if (characteristic["@type"] === ADDRESS_CHARACTERISTIC_TYPE) {
            return (
                <AddressCharacteristic
                    characteristic={characteristic}
                    uniqueKeyPrefix={uniqueKeyPrefix}
                    isDisabled={isDisabled}
                    addressModal={addressModal}
                    t={t}
                />
            );
        }

        if (hasValidUnit && sortedValues.length >= 3) {
            return (
                <RangeCharacteristic
                    sortedValues={sortedValues}
                    selectedValue={selectedValue}
                    uniqueKeyPrefix={uniqueKeyPrefix}
                    name={name}
                    isDisabled={isDisabled}
                    configurationItemId={configurationItemId}
                    actionType={actionType}
                    handleRangeChange={handleRangeChange}
                    handleRangeChangeEnd={handleRangeChangeEnd}
                    isProductCharacteristicValuePersisted={isProductCharacteristicValuePersisted}
                    shouldShowCurrentValue={shouldShowCurrentValue}
                    t={t}
                />
            );
        }

        return (
            <RadioCharacteristic
                sortedValues={sortedValues}
                selectedValue={selectedValue}
                uniqueKeyPrefix={uniqueKeyPrefix}
                name={name}
                isDisabled={isDisabled}
                configurationItemId={configurationItemId}
                actionType={actionType}
                handleCharacteristicUpdate={handleCharacteristicUpdate}
                isProductCharacteristicValuePersisted={isProductCharacteristicValuePersisted}
                shouldShowCurrentValue={shouldShowCurrentValue}
                t={t}
            />
        );
    }, [
        configurationItemId,
        configuration,
        checkAncestorsSelection,
        getSelectedValue,
        actionType,
        handleRangeChange,
        handleRangeChangeEnd,
        handleCharacteristicUpdate,
        isProductCharacteristicValuePersisted,
        shouldShowCurrentValue,
        shouldDisableInputs,
        addressModal,
        t,
        hasInstallmentCharges,
    ]);

    const configurableCharacteristics = useMemo(() => {
        return productConfiguration.configurationCharacteristic?.filter(
            (characteristic) => characteristic?.isConfigurable
        ) || [];
    }, [productConfiguration.configurationCharacteristic]);

    return (
        <>
            {configurableCharacteristics.map((characteristic) => (
                <div key={`characteristic-${configurationItemId}-${characteristic.name}`}>
                    {renderCharacteristic(characteristic)}
                </div>
            ))}
            {hasInstallmentCharges && (
                <InstallmentPricing
                    productConfiguration={productConfiguration}
                    configurationItemId={configurationItemId}
                />
            )}
        </>
    );
};