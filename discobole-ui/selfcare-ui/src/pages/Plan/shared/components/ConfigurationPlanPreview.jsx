// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useMemo} from 'react';
import processConfigurationPlanPreview from "../services/processConfigurationPlanPreview";
import {capitalizeFirstLetter, getActionColor} from "../../../../utlis/helpers";
import {
    calculatePricingForConfigurationItems,
    getSelectedConfigurationAction,
    summarizeConfigurationPrices
} from "../services/utils/utils";
import ModifyPlanModal from "../../EditPlan/components/PlanEditor/Modals/ModifyPlanModal";
import {AuthenticationTabs, Modal} from "../../../../components";
import {
    getFormattedValue,
    hasCharges,
    hasOnlyContractInConfiguration,
    renderPrices,
    shouldRenderPricing
} from "../../../../utlis/utils";
import OfferIncompatibilityModal from "../../SetUpPlan/components/Modals/OfferIncompatibilityModal";
import {getDisplayableSelectedCharacteristics, getFormattedSelectedDuration} from "../services/utils/configUtils";
import {PriceWithDiscountDetails} from "../../../../components/utils/PriceWithDiscountDetails";
import {setPricing} from "../../../../store/actions/pricingActions";
import {useConfiguration} from "../context/ConfigurationContext";

const ConfigurationPlanPreview = ({
                                      shouldDisableProceedButton,
                                      onProceedToOrder = () => {
                                      },
                                      showConfirmModal = false,
                                      handleConfirmOrder = () => {
                                      },
                                      hideConfirmModal = () => {
                                      },
                                      showLoginModal = false,
                                      hideLoginModal = () => {
                                      },
                                      onLoginSuccess = () => {
                                      },
                                      showUnqualifiedModal = false,
                                      hideUnqualifiedModal = () => {
                                      },
                                      shouldDisplayContract = false,
                                  }) => {
    const {configuration, dispatch, t} = useConfiguration();

    const {configurationStructure, terminatedConfigurationOffers} = processConfigurationPlanPreview(configuration);

    const includedBundles = useMemo(() =>
            configurationStructure.nestedBundles.filter(
                bundle => bundle.bundleType === "includedBundle"
            ),
        [configurationStructure.nestedBundles]
    );

    const optionalBundles = useMemo(() =>
            configurationStructure.nestedBundles.filter(
                bundle => bundle.bundleType === "optionalBundle"
            ),
        [configurationStructure.nestedBundles]
    );

    const directIncludedItems = configurationStructure.includedItems;

    const directOptionalItems = configurationStructure.optionalItems;

    const contractConfigItem = configurationStructure.configItem
        ? {...configurationStructure.configItem}
        : null;

    const extractSelectedCharacteristics = (item) => getDisplayableSelectedCharacteristics(item);

    const extractSelectedItems = (bundle) => {
        if (!bundle?.configItem?.productConfiguration?.isSelected) return [];
        return [
            bundle.configItem,
            ...bundle.includedItems.filter(item => item?.productConfiguration?.isSelected),
            ...bundle.optionalItems.filter(item => item?.productConfiguration?.isSelected),
            ...bundle.nestedBundles.flatMap(subBundle => extractSelectedItems(subBundle))
        ];
    };

    const allItems = useMemo(() => [
        configurationStructure.configItem?.productConfiguration?.isSelected
            ? configurationStructure.configItem
            : null,
        ...configurationStructure.includedItems.filter(item => item?.productConfiguration?.isSelected),
        ...configurationStructure.optionalItems.filter(item => item?.productConfiguration?.isSelected),
        ...configurationStructure.nestedBundles.flatMap(bundle => extractSelectedItems(bundle))
    ].filter(Boolean), [configurationStructure]);

    const pricing = useMemo(
        () => calculatePricingForConfigurationItems(allItems),
        [JSON.stringify(allItems)]
    );

    const hasContractSection =
        shouldDisplayContract &&
        !!contractConfigItem &&
        !hasOnlyContractInConfiguration(configurationStructure);

    const hasIncludedSection =
        Object.keys(includedBundles).length > 0 || directIncludedItems.length > 0;

    const hasOptionalSection =
        Object.values(optionalBundles).length > 0 || directOptionalItems.length > 0;

    const hasTerminatedSection = terminatedConfigurationOffers.length > 0;

    const hasVisibleSections =
        hasContractSection || hasIncludedSection || hasOptionalSection || hasTerminatedSection;

    const hasSelectedAction = useMemo(() =>
            allItems.some(item =>
                Boolean(getSelectedConfigurationAction(item?.productConfiguration))
            ),
        [allItems]
    );

    const canProceed = shouldDisableProceedButton !== undefined
        ? !shouldDisableProceedButton
        : (hasVisibleSections && hasSelectedAction);

    useEffect(() => {
        dispatch(setPricing(pricing));
    }, [dispatch, JSON.stringify(pricing)]);

    const renderItems = (items = []) => {
        const selectedItems = items.filter(
            (item) => item?.productConfiguration?.isSelected
        );
        const sortedItems = [...selectedItems].sort((a, b) => {
            const nameA =
                a?.productConfiguration?.productOffering?.name?.toLowerCase() || "";
            const nameB =
                b?.productConfiguration?.productOffering?.name?.toLowerCase() || "";
            return nameA.localeCompare(nameB);
        });
        if (!sortedItems.length) {
            return null;
        }

        return (
            <ul className="list-group list-group-flush">
                {sortedItems.map((item) => {
                    const keyId = item?.id;
                    const configurationPrices = item?.productConfiguration?.configurationPrice;
                    const {currentPrices, basePrices, discounts} = summarizeConfigurationPrices(configurationPrices);
                    const formattedConfigurationTerm = getFormattedSelectedDuration(item?.productConfiguration?.configurationTerm);

                    const characteristics = extractSelectedCharacteristics(item);
                    const configurationAction = getSelectedConfigurationAction(item?.productConfiguration);

                    return (
                        <li key={keyId} className="list-group-item benefit-item">
                            <div className="d-flex justify-content-between align-items-center">
                                <div className="flex-grow-1 text-wrap">
                                    <span className="text-break">
                                        {item?.productConfiguration?.productOffering?.name}
                                    </span>
                                </div>
                                <div className="d-flex align-items-center flex-shrink-0 ms-3">
                                    <div className="d-flex align-items-center me-2">
                                        {configurationAction && (
                                            <span
                                                className={`tag tag-sm action-type ${getActionColor(configurationAction)} default`}
                                            >
                                                <em className="icon-done_modifier action-icon"></em>
                                                {capitalizeFirstLetter(configurationAction)}
                                            </span>
                                        )}
                                    </div>
                                    <span className="fw-bold">
                                        {currentPrices.map((priceObj, index) => (
                                            <span key={index}>
                                                {priceObj.totalPrice}
                                                {priceObj.applicationOffset && (
                                                    <small className="fw-bold">
                                                        {" "}
                                                        {t("plan.planPreview.applicationOffset", {
                                                            offset: priceObj.applicationOffset.offset,
                                                            unit: priceObj.applicationOffset.unit
                                                        })}
                                                    </small>
                                                )}
                                                {priceObj.applicationDuration && (
                                                    <small className="fw-bold">
                                                        {" "}
                                                        {priceObj.applicationDuration}
                                                    </small>
                                                )}
                                                {index < currentPrices.length - 1 ? ' + ' : ''}
                                            </span>
                                        ))}
                                        {formattedConfigurationTerm && (
                                            <small className="fw-bold"> ({formattedConfigurationTerm})</small>
                                        )}
                                    </span>
                                </div>
                            </div>
                            <div className="description mt-1">
                                <div className="mb-0">
                                    <small>
                                        {characteristics
                                            .filter(
                                                (char) =>
                                                    char.value !== undefined &&
                                                    char.value !== null &&
                                                    char.value !== ""
                                            )
                                            .map((char, i) => (
                                                <div key={i}>
                                                    <strong>{char.name}:</strong> {getFormattedValue(char.value, t)} {char.unit}
                                                    <br/>
                                                </div>
                                            ))}
                                    </small>
                                </div>
                                {discounts.length > 0 && (
                                    <div className="description mt-1 d-flex justify-content-between">
                                        <small className="text-muted">{t("common.beforeDiscount")}
                                            <small
                                                className="tax-included-label"> {" "}({t("common.taxExcluded")})</small>
                                        </small>
                                        <span className="fw-bold text-muted">{basePrices}</span>
                                    </div>
                                )}
                                {discounts.length > 0 && (
                                    <div className="description mt-1 d-flex justify-content-between">
                                        <small className="text-danger">{t("common.discount")}</small>
                                        <span className="fw-bold text-danger">
                                            {discounts.map((priceObj, index) => (
                                                <span key={index}>
                                                    {priceObj.totalPrice}
                                                    {priceObj.applicationOffset && (
                                                        <small className="fw-bold">
                                                            {" "} {t("plan.planPreview.applicationOffset", {
                                                            offset: priceObj.applicationOffset.offset,
                                                            unit: priceObj.applicationOffset.unit
                                                        })}
                                                        </small>
                                                    )}
                                                    {priceObj.applicationDuration && (
                                                        <small className="fw-bold">
                                                            {" "} {priceObj.applicationDuration}
                                                        </small>
                                                    )}
                                                    {index < discounts.length - 1 ? ' + ' : ''}
                                                </span>
                                            ))}
                                        </span>
                                    </div>
                                )}
                            </div>
                        </li>
                    );
                })}
            </ul>
        );
    };

    const renderItemList = (bundlesObj, directItems, title, isOptional = false) => {
        const bundleArray = Object.values(bundlesObj || {});

        const filteredBundleArray = bundleArray.filter(bundle => {
            const {
                includedItems = [],
                optionalItems = [],
                deviceItems = [],
                nestedBundles = []
            } = bundle;
            return includedItems.length > 0 || optionalItems.length > 0 || deviceItems.length > 0 || nestedBundles.length > 0;
        });

        if (!filteredBundleArray.length && !directItems.length) return null;

        const sortedBundleArray = [...filteredBundleArray].sort((a, b) => {
            const nameA = a?.configItem?.productConfiguration?.productOffering?.name || "";
            const nameB = b?.configItem?.productConfiguration?.productOffering?.name || "";
            return nameA.localeCompare(nameB);
        });

        return (
            <div className="p-3">
                <h6 className="fs-6 mb-1 text-primary">{title}</h6>
                {sortedBundleArray.map((bundle, index, array) => {
                    const {
                        configItem,
                        includedItems = [],
                        optionalItems = [],
                        deviceItems = [],
                        nestedBundles = []
                    } = bundle;
                    const {productConfiguration} = configItem || {};
                    const {productOffering, configurationPrice = []} = productConfiguration || {};
                    const bundleName = productOffering?.name;
                    const {currentPrices, basePrices, discounts} = summarizeConfigurationPrices(configurationPrice);
                    const formattedConfigurationTerm = getFormattedSelectedDuration(productConfiguration.configurationTerm);
                    const configurationAction = getSelectedConfigurationAction(productConfiguration);

                    return (
                        <div key={configItem?.id} className="preview-items-group">
                            <div className="d-flex justify-content-between align-items-center">
                                <h6 className="fs-6 mb-0">{bundleName}</h6>
                                <div className="d-flex align-items-center flex-shrink-0 ms-3">
                                    {configurationAction && (
                                        <span
                                            className={`tag tag-sm action-type ${getActionColor(configurationAction)} default me-2`}>
                                            <em className="icon-done_modifier action-icon"></em>
                                            {capitalizeFirstLetter(configurationAction)}
                                        </span>
                                    )}
                                    <span className="fw-bold">
                                        {currentPrices.map((priceObj, index) => (
                                            <span key={index}>
                                                {priceObj.totalPrice}
                                                {priceObj.applicationOffset && (
                                                    <small>
                                                        {" "}
                                                        {t("plan.planPreview.applicationOffset", {
                                                            offset: priceObj.applicationOffset.offset,
                                                            unit: priceObj.applicationOffset.unit
                                                        })}
                                                    </small>
                                                )}
                                                {priceObj.applicationDuration && (
                                                    <small>
                                                        {" "}
                                                        {priceObj.applicationDuration}
                                                    </small>
                                                )}
                                                {index < currentPrices.length - 1 ? ' + ' : ''}
                                            </span>
                                        ))}
                                        {formattedConfigurationTerm && (
                                            <small className="fw-bold"> ({formattedConfigurationTerm})</small>
                                        )}
                                    </span>
                                </div>
                            </div>
                            <div className="description mt-1">
                                {discounts.length > 0 && (
                                    <div className="description mt-1 d-flex justify-content-between">
                                        <small className="text-muted">{t("common.beforeDiscount")}
                                            <small
                                                className="tax-included-label"> {" "}({t("common.taxExcluded")})</small>
                                        </small>
                                        <span className="fw-bold text-muted">{basePrices}</span>
                                    </div>
                                )}
                                {discounts.length > 0 && (
                                    <div className="description mt-1 d-flex justify-content-between">
                                        <small className="text-danger">{t("common.discount")}</small>
                                        <span className="fw-bold text-danger">
                                            {discounts.map((priceObj, index) => (
                                                <span key={index}>
                                                    {priceObj.totalPrice}
                                                    {priceObj.applicationOffset && (
                                                        <small className="fw-bold">
                                                            {" "} {t("plan.planPreview.applicationOffset", {
                                                            offset: priceObj.applicationOffset.offset,
                                                            unit: priceObj.applicationOffset.unit
                                                        })}
                                                        </small>
                                                    )}
                                                    {priceObj.applicationDuration && (
                                                        <small className="fw-bold">
                                                            {" "} {priceObj.applicationDuration}
                                                        </small>
                                                    )}
                                                    {index < discounts.length - 1 ? ' + ' : ''}
                                                </span>
                                            ))}
                                        </span>
                                    </div>
                                )}
                            </div>
                            {renderItems(includedItems)}
                            {renderItems(optionalItems)}
                            {renderItems(deviceItems)}
                            {nestedBundles.length > 0 &&
                                [...nestedBundles]
                                    .sort((a, b) => {
                                        const nameA =
                                            a?.configItem?.productConfiguration?.productOffering?.name;
                                        const nameB =
                                            b?.configItem?.productConfiguration?.productOffering?.name;
                                        return nameA.localeCompare(nameB);
                                    })
                                    .map((nestedBundle) => renderSubBundle(nestedBundle))
                            }
                            {index < array.length - 1 && <hr className="my-2 border-1"/>}
                        </div>
                    );
                })}
                {directItems.length > 0 && (() => {
                    const sortedDirect = [...directItems].sort((a, b) =>
                        (a?.configItem?.name || "").localeCompare(b?.configItem?.name || "")
                    );
                    const shouldDisplayHr = isOptional
                        ? Object.values(bundlesObj || {}).some(b => b.configItem?.productConfiguration?.isSelected)
                        : Object.keys(bundlesObj || {}).length > 0;
                    return (
                        <div>
                            {shouldDisplayHr && <hr className="my-2 border-1"/>}
                            <h6 className="fs-6 mb-0 fw-semibold ms-1 py-1">
                                {isOptional ? t("plan.sections.optionalOffers") : t("plan.sections.includedOffers")}
                            </h6>
                            {renderItems(sortedDirect)}
                        </div>
                    );
                })()}
            </div>
        );
    };

    const renderSubBundle = (subBundle) => {
        const {
            configItem,
            includedItems = [],
            optionalItems = [],
            nestedBundles = []
        } = subBundle;
        const {productConfiguration} = configItem || {};
        const {productOffering, configurationPrice = []} = productConfiguration || {};
        const bundleName = productOffering?.name;
        if (
            !configItem?.productConfiguration?.isSelected ||
            (includedItems.length === 0 && optionalItems.length === 0 && nestedBundles.length === 0)
        ) return null;

        const {currentPrices, basePrices, discounts} = summarizeConfigurationPrices(configurationPrice);
        const formattedConfigurationTerm = getFormattedSelectedDuration(productConfiguration.configurationTerm);

        const configurationAction = getSelectedConfigurationAction(productConfiguration);

        return (
            <li className="list-group-item benefit-item py-1" key={configItem?.id}>
                <div className="d-flex justify-content-between align-items-center">
                    <div className="flex-grow-1 text-wrap">
                        <span className="text-break">{bundleName}</span>
                    </div>
                    <div className="d-flex align-items-center flex-shrink-0 ms-3">
                        <div className="d-flex align-items-center me-2">
                            {configurationAction && (
                                <span
                                    className={`tag tag-sm action-type ${getActionColor(configurationAction)} default`}
                                >
                                    <em className="icon-done_modifier action-icon"></em>
                                    {capitalizeFirstLetter(configurationAction)}
                                </span>
                            )}
                        </div>
                        <span className="fw-bold">
                            {currentPrices.map((priceObj, index) => (
                                <span key={index}>
                                    {priceObj.totalPrice}
                                    {priceObj.applicationOffset && (
                                        <small className="fw-bold">
                                            {" "}
                                            {t("plan.planPreview.applicationOffset", {
                                                offset: priceObj.applicationOffset.offset,
                                                unit: priceObj.applicationOffset.unit
                                            })}
                                        </small>
                                    )}
                                    {priceObj.applicationDuration && (
                                        <small className="fw-bold">
                                            {" "}
                                            {priceObj.applicationDuration}
                                        </small>
                                    )}
                                    {index < currentPrices.length - 1 ? ' + ' : ''}
                                </span>
                            ))}
                            {formattedConfigurationTerm && (
                                <small className="fw-bold"> ({formattedConfigurationTerm})</small>
                            )}
                        </span>
                    </div>
                </div>
                {discounts.length > 0 && (
                    <div className="description mt-1 d-flex justify-content-between">
                        <small className="text-muted">{t("common.beforeDiscount")}
                            <small className="tax-included-label"> {" "}({t("common.taxExcluded")})</small>
                        </small>
                        <span className="fw-bold text-muted">{basePrices}</span>
                    </div>
                )}
                {discounts.length > 0 && (
                    <div className="description mt-1 d-flex justify-content-between">
                        <small className="text-danger">{t("common.discount")}</small>
                        <span className="fw-bold text-danger">
                            {discounts.map((priceObj, index) => (
                                <span key={index}>
                                    {priceObj.totalPrice}
                                    {priceObj.applicationOffset && (
                                        <small className="fw-bold">
                                            {" "} {t("plan.planPreview.applicationOffset", {
                                            offset: priceObj.applicationOffset.offset,
                                            unit: priceObj.applicationOffset.unit
                                        })}
                                        </small>
                                    )}
                                    {priceObj.applicationDuration && (
                                        <small className="fw-bold">
                                            {" "} {priceObj.applicationDuration}
                                        </small>
                                    )}
                                    {index < discounts.length - 1 ? ' + ' : ''}
                                </span>
                            ))}
                        </span>
                    </div>
                )}
                <div className="description mt-1">
                    <ul className="list-group list-group-flush">
                        {includedItems.length > 0 && includedItems.map((item) => renderSubItem(item, "Included"))}
                        {optionalItems.length > 0 && optionalItems.map((item) => renderSubItem(item, "Optional"))}
                        {nestedBundles.length > 0 && nestedBundles.map((nestedBundle) => renderSubBundle(nestedBundle))}
                    </ul>
                </div>
            </li>
        );
    };

    const renderSubItem = (item, type) => {
        const {productConfiguration} = item || {};
        const {productOffering, configurationPrice = [], isSelected} = productConfiguration || {};
        const itemName = productOffering?.name || `${type} Item`;
        if (!isSelected) return null;

        const {currentPrices, basePrices, discounts} = summarizeConfigurationPrices(configurationPrice);
        const formattedConfigurationTerm = getFormattedSelectedDuration(productConfiguration.configurationTerm);

        const characteristics = extractSelectedCharacteristics(item);
        const configurationAction = getSelectedConfigurationAction(productConfiguration);

        return (
            <li className="list-group-item border-0 py-1 px-2" key={item?.id}>
                <div className="d-flex justify-content-between align-items-center">
                    <div className="flex-grow-1 text-wrap">
                        <span className="text-muted">{itemName}</span>
                    </div>
                    <div className="d-flex align-items-center flex-shrink-0 ms-3">
                        <div className="d-flex align-items-center me-2">
                            {configurationAction && (
                                <span
                                    className={`tag tag-sm action-type ${getActionColor(configurationAction)} default`}
                                >
                                    <em className="icon-done_modifier action-icon"></em>
                                    {capitalizeFirstLetter(configurationAction)}
                                </span>
                            )}
                        </div>
                        <span className="fw-bold">
                            {currentPrices.map((priceObj, index) => (
                                <span key={index}>
                                    {priceObj.totalPrice}
                                    {priceObj.applicationOffset && (
                                        <small className="fw-bold">
                                            {" "}
                                            {t("plan.planPreview.applicationOffset", {
                                                offset: priceObj.applicationOffset.offset,
                                                unit: priceObj.applicationOffset.unit
                                            })}
                                        </small>
                                    )}
                                    {priceObj.applicationDuration && (
                                        <small className="fw-bold">
                                            {" "}
                                            {priceObj.applicationDuration}
                                        </small>
                                    )}
                                    {index < currentPrices.length - 1 ? ' + ' : ''}
                                </span>
                            ))}
                            {formattedConfigurationTerm && (
                                <small className="fw-bold"> ({formattedConfigurationTerm})</small>
                            )}
                        </span>
                    </div>
                </div>
                {characteristics.length > 0 && (
                    <div className="description mt-1">
                        <div className="mb-0">
                            <small>
                                {characteristics
                                    .filter(char => char.value !== undefined && char.value !== null && char.value !== "")
                                    .map((char, i) => (
                                        <div key={i}>
                                            <strong>{char.name}:</strong> {getFormattedValue(char.value, t)} {char.unit}
                                            <br/>
                                        </div>
                                    ))}
                            </small>
                        </div>
                    </div>
                )}
                {discounts.length > 0 && (
                    <div className="description mt-1 d-flex justify-content-between">
                        <small className="text-muted">{t("common.beforeDiscount")}
                            <small className="tax-included-label"> {" "}({t("common.taxExcluded")})</small>
                        </small>
                        <span className="fw-bold text-muted">{basePrices}</span>
                    </div>
                )}
                {discounts.length > 0 && (
                    <div className="description mt-1 d-flex justify-content-between">
                        <small className="text-danger">{t("common.discount")}</small>
                        <span className="fw-bold text-danger">
                            {discounts.map((priceObj, index) => (
                                <span key={index}>
                                    {priceObj.totalPrice}
                                    {priceObj.applicationOffset && (
                                        <small className="fw-bold">
                                            {" "} {t("plan.planPreview.applicationOffset", {
                                            offset: priceObj.applicationOffset.offset,
                                            unit: priceObj.applicationOffset.unit
                                        })}
                                        </small>
                                    )}
                                    {priceObj.applicationDuration && (
                                        <small className="fw-bold">
                                            {" "} {priceObj.applicationDuration}
                                        </small>
                                    )}
                                    {index < discounts.length - 1 ? ' + ' : ''}
                                </span>
                            ))}
                        </span>
                    </div>
                )}
            </li>
        );
    };

    return (
        <div className="card">
            <div className="card-body p-1">
                <ul className="list-group list-group-flush benefit-items-wrapper m-0">
                    {shouldDisplayContract && contractConfigItem && !hasOnlyContractInConfiguration(configurationStructure) && (() => {
                        const configurationPrices = contractConfigItem.productConfiguration?.configurationPrice;
                        const offeringName = contractConfigItem.productConfiguration?.productOffering?.name;
                        const {
                            currentPrices,
                            basePrices,
                            discounts
                        } = summarizeConfigurationPrices(configurationPrices);
                        const formattedConfigurationTerm = getFormattedSelectedDuration(contractConfigItem.productConfiguration.configurationTerm);
                        const configurationAction = getSelectedConfigurationAction(contractConfigItem.productConfiguration);

                        return (
                            <>
                                <div className="p-3 py-2">
                                    <li className="list-group-item benefit-item ps-0">
                                        <div className="d-flex justify-content-between align-items-center">
                                            {offeringName && <h6 className="fs-6 mb-0">{offeringName}</h6>}
                                            <div className="d-flex align-items-center flex-shrink-0 ms-3">
                                                {configurationAction && (
                                                    <span
                                                        className={`tag tag-sm action-type ${getActionColor(configurationAction)} default me-2`}>
                                                        <em className="icon-done_modifier action-icon"></em>
                                                        {capitalizeFirstLetter(configurationAction)}
                                                    </span>
                                                )}
                                                <span className="fw-bold">
                                                    {currentPrices.map((priceObj, index) => (
                                                        <span key={index}>
                                                            {priceObj.totalPrice}
                                                            {priceObj.applicationOffset && (
                                                                <small className="fw-bold">
                                                                    {" "}
                                                                    {t("plan.planPreview.applicationOffset", {
                                                                        offset: priceObj.applicationOffset.offset,
                                                                        unit: priceObj.applicationOffset.unit
                                                                    })}
                                                                </small>
                                                            )}
                                                            {priceObj.applicationDuration && (
                                                                <small className="fw-bold">
                                                                    {" "}
                                                                    {priceObj.applicationDuration}
                                                                </small>
                                                            )}
                                                            {index < currentPrices.length - 1 ? ' + ' : ''}
                                                        </span>
                                                    ))}
                                                    {formattedConfigurationTerm && (
                                                        <small
                                                            className="fw-bold"> ({formattedConfigurationTerm})</small>
                                                    )}
                                                </span>
                                            </div>
                                        </div>
                                        {discounts.length > 0 && (
                                            <div className="description mt-1 d-flex justify-content-between">
                                                <small className="text-muted">{t("common.beforeDiscount")}
                                                    <small
                                                        className="tax-included-label"> {" "}({t("common.taxExcluded")})</small>
                                                </small>
                                                <span className="fw-bold text-muted">{basePrices}</span>
                                            </div>
                                        )}
                                        {discounts.length > 0 && (
                                            <div className="description mt-1 d-flex justify-content-between">
                                                <small className="text-danger">{t("common.discount")}</small>
                                                <span className="fw-bold text-danger">
                                                    {discounts.map((priceObj, index) => (
                                                        <span key={index}>
                                                            {priceObj.totalPrice}
                                                            {priceObj.applicationOffset && (
                                                                <small className="fw-bold">
                                                                    {" "} {t("plan.planPreview.applicationOffset", {
                                                                    offset: priceObj.applicationOffset.offset,
                                                                    unit: priceObj.applicationOffset.unit
                                                                })}
                                                                </small>
                                                            )}
                                                            {priceObj.applicationDuration && (
                                                                <small className="fw-bold">
                                                                    {" "} {priceObj.applicationDuration}
                                                                </small>
                                                            )}
                                                            {index < discounts.length - 1 ? ' + ' : ''}
                                                        </span>
                                                    ))}
                                                </span>
                                            </div>
                                        )}
                                    </li>
                                </div>
                                <hr className="my-0 border-2"/>
                            </>
                        );
                    })()}
                    {(Object.keys(includedBundles).length > 0 || directIncludedItems.length > 0) &&
                        renderItemList(includedBundles, directIncludedItems, t("plan.planPreview.included"), false)
                    }
                    {(Object.keys(includedBundles).length > 0 || directIncludedItems.length > 0) &&
                        ((Object.values(optionalBundles).length > 0) || directOptionalItems.length > 0) &&
                        <hr className="m-0"/>
                    }
                    {(Object.values(optionalBundles).length > 0 || directOptionalItems.length > 0) &&
                        renderItemList(optionalBundles, directOptionalItems, t("plan.planPreview.options"), true)
                    }
                    {terminatedConfigurationOffers.length > 0 && (
                        <div>
                            <hr className="my-2 border-1"/>
                            <div className="p-3">
                                <h6 className="fs-6 mb-1 text-primary">
                                    Terminated Offers
                                </h6>
                                {renderItems(terminatedConfigurationOffers)}
                            </div>
                        </div>
                    )}
                </ul>
                {shouldRenderPricing(pricing) && (
                    <>
                        <hr className="m-0"/>
                        <div className="p-3">
                            {hasCharges(pricing.current) && (
                                <div className="d-flex justify-content-between align-items-center">
                                    <div className="me-auto">
                                        <div className="fw-bold">
                                            {t("common.total")}
                                            <small className="tax-included-label"> ({t("common.taxIncluded")})</small>
                                        </div>
                                    </div>
                                    <span className="fw-bold">
                                        {renderPrices(pricing.current, pricing.currency)}
                                    </span>
                                </div>
                            )}
                            <PriceWithDiscountDetails pricing={pricing}/>
                        </div>
                    </>
                )}
            </div>
            <div className="card-footer">
                <div className="d-grid">
                    <button
                        className="btn btn-primary"
                        onClick={onProceedToOrder}
                        disabled={!canProceed}
                    >
                        {t("actions.proceedToOrder")}
                    </button>
                </div>
            </div>

            {showConfirmModal && (
                <Modal
                    show={showConfirmModal}
                    title={t("plan.confirmations.modify.proceed")}
                    body={
                        <ModifyPlanModal
                            hideModal={hideConfirmModal}
                            onConfirm={handleConfirmOrder}
                        />
                    }
                    onClose={hideConfirmModal}
                    hideFooter={true}
                    className="ConfirmOrderModal"
                />
            )}

            {showLoginModal && (
                <Modal
                    show={showLoginModal}
                    body={
                        <AuthenticationTabs
                            onLoginSuccess={onLoginSuccess}
                        />
                    }
                    onClose={hideLoginModal}
                    hideFooter
                    className="login-modal"
                />
            )}

            {showUnqualifiedModal && (
                <Modal
                    show={showUnqualifiedModal}
                    body={<OfferIncompatibilityModal/>}
                    onClose={hideUnqualifiedModal}
                    hideFooter
                />
            )}
        </div>
    );
};

export default ConfigurationPlanPreview;