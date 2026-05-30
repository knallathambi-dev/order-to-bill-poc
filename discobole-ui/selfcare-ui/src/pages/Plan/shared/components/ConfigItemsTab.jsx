// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from "react";
import ConfigItemGroup from "./ConfigItemGroup";
import NestedBundles from "./NestedBundles";
import {addFirstSelectableItem, getConfigurationProperty} from "../services/utils/utils";
import {performActionAndGetCharacteristics} from "../services/productConfigurationService";
import {useConfiguration} from "../context/ConfigurationContext";
import Commitment from "./Commitment";
import ContractConfigItem from "./ContractConfigItem";
import PriceDisplay from "./PriceDisplay";
import {getFormattedSelectedDuration} from "../services/utils/configUtils";
import {getCurrentPrices} from "../../../../utlis/utils";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {toast} from "react-toastify";

const ConfigItemsTab = ({
                            mode = "included",
                            variant = "setup",
                            contract = {},
                            bundles = [],
                            directConfigItems = [],
                            onAction,
                            actionLabel,
                        }) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, tNotification} = useConfiguration();
    const isIncluded = mode === "included";

    const handleBundleToggle = useCallback(async (bundleConfigurationId, currentIsSelected) => {
        const result = await performActionAndGetCharacteristics({
            configurationId: configuration.id,
            configurationItemId: bundleConfigurationId,
            characteristic: null,
            isSelected: !currentIsSelected,
            relatedParty,
            actionType: "add",
            dispatch,
            tNotification,
            onConfigurationChange,
        });

        if (!result) {
            toast.error(tNotification("plan.updateCharacteristicFailed"));
        }
    }, [configuration.id, relatedParty, dispatch, tNotification, onConfigurationChange]);

    const accordionId = isIncluded ? "accordionIncludedBundles" : "accordionOptionalBundles";

    return (
        <div className={isIncluded ? "included mt-3" : "options mt-3"}>
            <div className="row">
                <div className="col-md-12">
                    <div className="accordion accordion-sm included-accordion" id={accordionId}>
                        {isIncluded && <ContractConfigItem contract={contract}/>}

                        {bundles
                            .sort((a, b) => a.offeringName.toLowerCase().localeCompare(b.offeringName.toLowerCase()))
                            .map((bundle) => {
                                const upperLimit = bundle.upperLimit;
                                const displayName = bundle.offeringName;

                                if (upperLimit > 1) {
                                    return (
                                        <MultiPurchaseBundle
                                            key={`bundle-${bundle.id}`}
                                            bundle={bundle}
                                            displayName={displayName}
                                            upperLimit={upperLimit}
                                            handleBundleToggle={handleBundleToggle}
                                            variant={variant}
                                        />
                                    );
                                }

                                return (
                                    <SingleBundle
                                        key={`bundle-${bundle.items[0]?.configItem?.id}`}
                                        bundle={bundle}
                                        handleBundleToggle={handleBundleToggle}
                                        variant={variant}
                                    />
                                );
                            })}

                        {bundles.length > 0 && directConfigItems.length > 0 && (
                            <hr className="my-2"/>
                        )}

                        {directConfigItems.length > 0 && (
                            <DirectConfigItemsSection
                                mode={mode}
                                directConfigItems={directConfigItems}
                                variant={variant}
                            />
                        )}
                    </div>
                </div>

                {onAction && (
                    <div className="d-flex justify-content-end mt-4">
                        <button
                            className={`btn ${isIncluded ? "btn-primary" : "btn-secondary"}`}
                            type="button"
                            onClick={onAction}
                        >
                            {actionLabel}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

const MultiPurchaseBundleItem = ({groupItem, idx, handleBundleToggle, variant, t}) => {
    const configItem = groupItem.configItem;
    const bundleConfigId = configItem?.id || `group-${idx}`;
    const bundleName = configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(configItem?.productConfiguration?.configurationPrice);
    const formattedDuration = getFormattedSelectedDuration(configItem?.productConfiguration?.configurationTerm);
    const isSelected = getConfigurationProperty(configItem?.productConfiguration, 'isSelected');
    const isSelectable = getConfigurationProperty(configItem?.productConfiguration, 'isSelectable');

    const nestedIncluded = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle")
    ) || [];
    const nestedOptional = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle")
    ) || [];

    const collapseId = `collapseSubitem-${bundleConfigId}`;
    const accordionId = `accordionBundle-${bundleConfigId}`;

    return (
        <div id={collapseId} className="accordion-collapse collapse show" key={collapseId}>
            <div className="accordion-body p-0 mt-1 mb-4">
                <div className="accordion multi-purchase-accordion" id={accordionId}>
                    <div className="card">
                        <div className="card-body py-2">
                            <div className="accordion-item">
                                <h4 className="accordion-header position-relative">
                                    <button
                                        className="accordion-button optional-accordion-button"
                                        type="button"
                                        aria-expanded={true}
                                        aria-controls={`collapseSubitemOne-${bundleConfigId}`}
                                    >
                                        <div
                                            className="d-flex justify-content-between align-items-center w-100">
                                            <h5 className="mb-0 fs-5">
                                                {bundleName}
                                                <small className="text-muted">
                                                    {" "}( {t("common.selectToOrder")} )
                                                </small>
                                            </h5>
                                            <div className="d-flex align-items-center">
                                                {isSelected && (
                                                    <PriceDisplay
                                                        currentPrices={currentPrices}
                                                        formattedDuration={formattedDuration}
                                                        className="me-3 text-muted fw-bold fs-5"
                                                    />
                                                )}
                                                <div className="d-flex align-items-center justify-content-center px-2">
                                                    {isSelectable ? (
                                                        <input
                                                            className="form-check-input check-option m-0"
                                                            type="checkbox"
                                                            id={`checkboxOptionalBundle-${bundleConfigId}`}
                                                            disabled={!isSelectable}
                                                            checked={isSelected}
                                                            onChange={() => handleBundleToggle(bundleConfigId, isSelected)}
                                                            onClick={(e) => e.stopPropagation()}
                                                        />
                                                    ) : (
                                                        <div className="check-circle m-0">
                                                            <em className="icon-checkbox_tick"></em>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    </button>
                                </h4>
                                <div
                                    id={`collapseSubitemOne-${bundleConfigId}`}
                                    className="accordion-collapse collapse show"
                                >
                                    <div className="accordion-body p-1">
                                        <div className="bundle-item-wrapper">
                                            <div className="bundle-container">
                                                <div className="d-grid gap-2">
                                                    {isSelected &&
                                                        <Commitment configItem={configItem}/>}
                                                    <ConfigItemGroup
                                                        variant={variant}
                                                        mode="included"
                                                        configItems={groupItem.includedItems || []}
                                                        isSubBundle={false}
                                                    />
                                                    <ConfigItemGroup
                                                        variant={variant}
                                                        mode="optional"
                                                        configItems={groupItem.optionalItems || []}
                                                        isSubBundle={false}
                                                    />
                                                    <NestedBundles
                                                        variant={variant}
                                                        bundles={nestedIncluded}
                                                        isOptional={false}
                                                        onToggle={handleBundleToggle}
                                                    />
                                                    <NestedBundles
                                                        variant={variant}
                                                        bundles={nestedOptional}
                                                        isOptional={true}
                                                        onToggle={handleBundleToggle}
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const MultiPurchaseBundle = ({bundle, displayName, upperLimit, handleBundleToggle, variant}) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, t, tNotification} = useConfiguration();

    const selectedCount = bundle.items.filter(
        (item) => item.configItem.productConfiguration.isSelected
    ).length;
    const isDisabled = selectedCount === upperLimit;

    return (
        <div className="accordion-item" key={`bundle-${bundle.id}`}>
            <h2 className="accordion-header">
                <button className="accordion-button optional-accordion-button" type="button">
                    <div className="d-flex justify-content-between align-items-center w-100">
                        <h5 className="mb-0 fs-4">
                            {displayName}
                            <small className="text-muted">
                                {" "}( {t("common.addUpTo")} {upperLimit} )
                            </small>
                        </h5>
                        <div className="d-flex align-items-center me-3">
                            <div className="me-2 fs-6 bundles-count">
                                {selectedCount}/{upperLimit}
                            </div>
                            <div
                                className={`add-multiple-button ${isDisabled ? "disabled" : ""}`}
                                onClick={() =>
                                    addFirstSelectableItem(
                                        bundle.items, true,
                                        configuration.id, relatedParty, dispatch, tNotification,
                                        onConfigurationChange
                                    )
                                }
                            >
                                <em className="icon-Add"></em>
                            </div>
                        </div>
                    </div>
                </button>
            </h2>
            {bundle.items
                .filter((groupItem) => groupItem.configItem.productConfiguration.isSelected)
                .map((groupItem, idx) => (
                    <MultiPurchaseBundleItem
                        key={groupItem.configItem?.id || `group-${idx}`}
                        groupItem={groupItem}
                        idx={idx}
                        handleBundleToggle={handleBundleToggle}
                        variant={variant}
                        t={t}
                    />
                ))}
        </div>
    );
};

const SingleBundle = ({bundle, handleBundleToggle, variant}) => {
    const [isOpen, setIsOpen] = useState(true);
    const bundleConfigId = bundle.items[0].configItem?.id;
    const bundleName = bundle.items[0].configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(bundle.items[0].configItem?.productConfiguration.configurationPrice);
    const formattedDuration = getFormattedSelectedDuration(bundle.items[0].configItem?.productConfiguration?.configurationTerm);
    const isSelected = getConfigurationProperty(bundle.items[0].configItem?.productConfiguration, 'isSelected');
    const isSelectable = getConfigurationProperty(bundle.items[0].configItem?.productConfiguration, 'isSelectable');

    const nestedIncludedBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle"));
    const nestedOptionalBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle"));

    const collapseId = `collapse-${bundleConfigId}`;

    return (
        <div className="accordion-item" key={`bundle-${bundleConfigId}`}>
            <h2
                className={`accordion-header ${
                    isSelectable
                        ? "d-flex justify-content-between align-items-center position-relative"
                        : "position-relative"
                }`}
            >
                <button
                    className={`accordion-button ${
                        isSelectable
                            ? "optional-accordion-button d-flex justify-content-between w-100"
                            : "included-accordion-button"
                    } ${!isOpen ? 'collapsed' : ''}`}
                    type="button"
                    aria-expanded={isOpen}
                    aria-controls={collapseId}
                    onClick={() => setIsOpen((prev) => !prev)}
                >
                    <h5 className="mb-0 fs-5">{bundleName}</h5>
                    {isSelected && (
                        <div className="bundle-offer-price">
                            <PriceDisplay
                                currentPrices={currentPrices}
                                formattedDuration={formattedDuration}
                                className={`${!isSelectable ? 'me-4' : 'me-2'} text-muted fw-bold fs-5`}
                            />
                        </div>
                    )}
                </button>
                {isSelectable && (
                    <div className="d-flex align-items-center">
                        <input
                            className="form-check-input check-option"
                            type="checkbox"
                            id={`checkboxOptionalBundle-${bundleConfigId}`}
                            disabled={!isSelectable}
                            checked={isSelected}
                            onChange={() => handleBundleToggle(bundleConfigId, isSelected)}
                        />
                    </div>
                )}
            </h2>
            <div id={collapseId} className={`accordion-collapse collapse ${isOpen ? 'show' : ''}`}>
                <div className="accordion-body pb-1 px-0">
                    <div className="bundle-container d-grid gap-2">
                        {isSelected && <Commitment configItem={bundle.items[0].configItem}/>}
                        <ConfigItemGroup
                            variant={variant}
                            mode="included"
                            configItems={bundle.items[0].includedItems || []}
                            isSubBundle={false}
                        />
                        <ConfigItemGroup
                            variant={variant}
                            mode="optional"
                            configItems={bundle.items[0].optionalItems || []}
                            isSubBundle={false}
                        />
                        <NestedBundles
                            variant={variant}
                            bundles={nestedIncludedBundles}
                            isOptional={false}
                            onToggle={handleBundleToggle}
                        />
                        <NestedBundles
                            variant={variant}
                            bundles={nestedOptionalBundles}
                            isOptional={true}
                            onToggle={handleBundleToggle}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

const DirectConfigItemsSection = ({mode, directConfigItems, variant}) => {
    const [isOpen, setIsOpen] = useState(true);
    const {t} = useTranslations();
    const isIncluded = mode === "included";
    const sectionLabel = isIncluded ? "IncludedConfigItems" : "OptionalConfigItems";

    return (
        <div className="accordion-item">
            <h2 className="accordion-header">
                <button
                    className={`accordion-button included-accordion-button ${!isOpen ? 'collapsed' : ''}`}
                    type="button"
                    aria-expanded={isOpen}
                    aria-controls={`collapseDirect${sectionLabel}`}
                    onClick={() => setIsOpen((prev) => !prev)}
                >
                    {isIncluded ? t("plan.sections.includedOffers") : t("plan.sections.optionalOffers")}
                </button>
            </h2>
            <div
                id={`collapseDirect${sectionLabel}`}
                className={`accordion-collapse collapse ${isOpen ? 'show' : ''}`}
            >
                <div className="accordion-body pb-1 px-0">
                    <div className="d-grid gap-2">
                        <ConfigItemGroup
                            variant={variant}
                            mode={mode}
                            configItems={directConfigItems || []}
                            isSubBundle={false}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ConfigItemsTab;