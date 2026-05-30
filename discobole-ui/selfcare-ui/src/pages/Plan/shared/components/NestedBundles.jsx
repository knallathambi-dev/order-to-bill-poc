// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import ConfigItemGroup from "./ConfigItemGroup";
import Commitment from "./Commitment";
import PriceDisplay from "./PriceDisplay";
import {addFirstSelectableItem, checkAncestorsSelection, getConfigurationProperty,} from "../services/utils/utils";
import {useConfiguration} from "../context/ConfigurationContext";
import {getCurrentPrices} from "../../../../utlis/utils";
import {getFormattedSelectedDuration} from "../services/utils/configUtils";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const MultiPurchaseNestedItem = ({groupItem, index, isOptional, onToggle, nestedIndex, variant}) => {
    const {t} = useTranslations();
    const [isOpen, setIsOpen] = useState(true);

    const bundleConfigId = groupItem.configItem?.id || `group-${index}`;
    const bundleName = groupItem.configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(groupItem.configItem?.productConfiguration?.configurationPrice);
    const formattedDuration = getFormattedSelectedDuration(groupItem.configItem?.productConfiguration?.configurationTerm);
    const isSelected = getConfigurationProperty(groupItem.configItem?.productConfiguration, 'isSelected');
    const isSelectable = getConfigurationProperty(groupItem.configItem?.productConfiguration, 'isSelectable');

    const nestedIncluded = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle")
    ) || [];
    const nestedOptional = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle")
    ) || [];

    return (
        <div id={`collapseSubitem-${bundleConfigId}`} className="accordion-collapse show" key={index}>
            <div className="accordion-body p-0 mt-1 mb-4">
                <div className="accordion multi-purchase-accordion" id={`accordionBundle-${bundleConfigId}`}>
                    <div className="card">
                        <div className="card-body py-2">
                            <div className="accordion-item">
                                <h4 className="accordion-header position-relative">
                                    <button
                                        className={`accordion-button no-select ${isOptional ? "optional-accordion-button" : ""} ${!isOpen ? "collapsed" : ""}`}
                                        type="button"
                                        aria-expanded={isOpen}
                                        aria-controls={`collapseSubitemOne-${bundleConfigId}`}
                                        onClick={() => setIsOpen((prev) => !prev)}
                                    >
                                        <div className="d-flex justify-content-between align-items-center w-100">
                                            <h5 className="mb-0 fs-5">
                                                {bundleName}
                                                <small className="text-muted">
                                                    {" "}( {t("common.selectToOrder")} )
                                                </small>
                                            </h5>
                                            <div className="d-flex align-items-center ms-auto">
                                                {isSelected && (
                                                    <PriceDisplay
                                                        currentPrices={currentPrices}
                                                        formattedDuration={formattedDuration}
                                                        className="me-3 text-muted fw-bold fs-5"
                                                    />
                                                )}
                                                {isSelectable ? (
                                                    <input
                                                        className={`${isOptional ? "" : "me-3 "}form-check-input check-option`}
                                                        type="checkbox"
                                                        id={`checkboxNested-${nestedIndex}`}
                                                        disabled={!isSelectable}
                                                        checked={isSelected}
                                                        onChange={() => onToggle(bundleConfigId, isSelected)}
                                                        onClick={(e) => e.stopPropagation()}
                                                    />
                                                ) : (
                                                    <small>
                                                        <em className="icon-arrow_up ms-2"></em>
                                                    </small>
                                                )}
                                            </div>
                                        </div>
                                    </button>
                                </h4>
                                <div
                                    id={`collapseSubitemOne-${bundleConfigId}`}
                                    className={`accordion-collapse collapse ${isOpen ? "show" : ""}`}
                                >
                                    <div className="accordion-body p-1">
                                        <div className="bundle-item-wrapper">
                                            <div className="bundle-container">
                                                <div className="d-grid gap-2">
                                                    {isSelected && <Commitment configItem={groupItem.configItem}/>}
                                                    <ConfigItemGroup variant={variant}
                                                                     mode="included"
                                                                     configItems={groupItem.includedItems || []}
                                                                     isSubBundle={false}
                                                    />
                                                    <ConfigItemGroup variant={variant}
                                                                     mode="optional"
                                                                     configItems={groupItem.optionalItems || []}
                                                                     isSubBundle={false}
                                                    />
                                                    <NestedBundles
                                                        variant={variant}
                                                        bundles={nestedIncluded}
                                                        isOptional={false}
                                                        onToggle={onToggle}
                                                    />
                                                    <NestedBundles
                                                        variant={variant}
                                                        bundles={nestedOptional}
                                                        isOptional={true}
                                                        onToggle={onToggle}
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

const MultiPurchaseNestedBundle = ({
                                       nestedBundle,
                                       nestedIndex,
                                       isOptional,
                                       onToggle,
                                       displayName,
                                       upperLimit,
                                       variant
                                   }) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, t, tNotification} = useConfiguration();

    const collapseId = `collapse-${nestedBundle.items[0]?.configItem?.id}`;
    const selectedCount = nestedBundle.items.filter(
        (item) => item.configItem.productConfiguration.isSelected
    ).length;
    const isDisabled = selectedCount === upperLimit;
    const isAncestorSelected = checkAncestorsSelection(
        nestedBundle.items[0].configItem?.id,
        configuration.computedProductConfigurationItem
    );

    return (
        <div
            className={`accordion-item ${!isAncestorSelected ? "opacity-50" : ""}`}
            key={`${isOptional ? "optional" : "included"}Nested-${nestedBundle.items[0]?.configItem?.id}`}
        >
            <h2 className="accordion-header">
                <button
                    className="accordion-button bg-body-secondary"
                    type="button"
                    aria-expanded={true}
                    aria-controls={collapseId}
                >
                    <div className="d-flex justify-content-between align-items-center w-100">
                        <h5 className="mb-0 fs-4">
                            {displayName}
                            <small className="text-muted">
                                {" "}( {t("common.addUpTo")} {upperLimit} )
                            </small>
                        </h5>
                        <div className="d-flex align-items-center">
                            <div className="me-2 fs-6 bundles-count">
                                {selectedCount}/{upperLimit}
                            </div>
                            <div
                                className={`add-multiple-button ${isDisabled ? 'disabled' : ''}`}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    addFirstSelectableItem(
                                        nestedBundle.items, true,
                                        configuration.id, relatedParty, dispatch, tNotification,
                                        onConfigurationChange
                                    );
                                }}
                            >
                                <em className="icon-Add"></em>
                            </div>
                        </div>
                    </div>
                </button>
            </h2>
            <div
                id={collapseId}
                className="accordion-collapse collapse show"
            >
                {isOptional ? (
                    nestedBundle.items
                        .filter((groupItem) => groupItem.configItem.productConfiguration.isSelected)
                        .map((groupItem, index) => (
                            <MultiPurchaseNestedItem
                                variant={variant}
                                key={index}
                                groupItem={groupItem}
                                index={index}
                                isOptional={isOptional}
                                onToggle={onToggle}
                                nestedIndex={nestedIndex}
                            />
                        ))
                ) : (
                    <div className="card mt-1">
                        <div className="card-body py-2">
                            {nestedBundle.items
                                .filter((groupItem) => groupItem.configItem.productConfiguration.isSelected)
                                .map((groupItem, index) => (
                                    <MultiPurchaseNestedItem
                                        variant={variant}
                                        key={index}
                                        groupItem={groupItem}
                                        index={index}
                                        isOptional={isOptional}
                                        onToggle={onToggle}
                                        nestedIndex={nestedIndex}
                                    />
                                ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

const SingleNestedBundle = ({nestedBundle, nestedIndex, isOptional, onToggle, variant}) => {
    const {configuration} = useConfiguration();
    const [isOpen, setIsOpen] = useState(true);

    const bundleConfigId = nestedBundle.items[0].configItem?.id;
    const nestedBundleName = nestedBundle.items[0].configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(nestedBundle.items[0].configItem?.productConfiguration?.configurationPrice);
    const formattedDuration = getFormattedSelectedDuration(nestedBundle.items[0].configItem?.productConfiguration?.configurationTerm);
    const isSelected = getConfigurationProperty(nestedBundle.items[0].configItem.productConfiguration, 'isSelected');
    const isSelectable = getConfigurationProperty(nestedBundle.items[0].configItem.productConfiguration, 'isSelectable');
    const isAncestorSelected = checkAncestorsSelection(
        nestedBundle.items[0].configItem?.id,
        configuration.computedProductConfigurationItem
    );

    const nestedIncluded = nestedBundle?.items
        ?.flatMap((item) => item?.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle")) || [];
    const nestedOptional = nestedBundle?.items
        ?.flatMap((item) => item?.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle")) || [];

    const collapseId = `collapse-${bundleConfigId}`;

    if (isOptional) {
        return (
            <div
                className={`accordion-item ${!isAncestorSelected ? "opacity-50" : ""}`}
                key={`optionalNested-${nestedIndex}`}
            >
                <h2 className="accordion-header">
                    <button
                        className={`accordion-button bg-body-secondary ${!isOpen ? "collapsed" : ""}`}
                        type="button"
                        aria-expanded={isOpen}
                        aria-controls={collapseId}
                        onClick={() => setIsOpen((prev) => !prev)}
                    >
                        <h5 className="mb-0 fs-4">
                            {nestedBundleName}
                            <small>
                                <em className="icon-arrow_up ms-2"></em>
                            </small>
                        </h5>
                        <div className="d-flex align-items-center ms-auto">
                            {isSelected && (
                                <PriceDisplay
                                    currentPrices={currentPrices}
                                    formattedDuration={formattedDuration}
                                    className="me-3 text-muted fw-bold fs-5"
                                />
                            )}
                            <input
                                className="form-check-input check-option"
                                type="checkbox"
                                id={`checkboxNested-${nestedIndex}`}
                                disabled={!isSelectable}
                                checked={isSelected}
                                onChange={() => onToggle(bundleConfigId, isSelected)}
                                onClick={(e) => e.stopPropagation()}
                            />
                        </div>
                    </button>
                </h2>
                <div id={collapseId} className={`accordion-collapse collapse ${isOpen ? "show" : ""}`}>
                    <div className="accordion-body p-3 d-grid gap-1">
                        {isSelected && <Commitment configItem={nestedBundle.items[0].configItem}/>}
                        <ConfigItemGroup variant={variant} mode="included"
                                         configItems={nestedBundle.items[0].includedItems || []} isSubBundle/>
                        <ConfigItemGroup variant={variant} mode="optional"
                                         configItems={nestedBundle.items[0].optionalItems || []} isSubBundle/>
                        <NestedBundles variant={variant} bundles={nestedIncluded} isOptional={false}
                                       onToggle={onToggle}/>
                        <NestedBundles variant={variant} bundles={nestedOptional} isOptional={true}
                                       onToggle={onToggle}/>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div
            key={`includedNested-${nestedIndex}`}
            className={`accordion-item ${!isAncestorSelected ? "opacity-50" : ""}`}
        >
            <h2 className="accordion-header">
                <button
                    className={`accordion-button bg-body-secondary ${!isOpen ? "collapsed" : ""}`}
                    type="button"
                    aria-expanded={isOpen}
                    aria-controls={collapseId}
                    onClick={() => setIsOpen((prev) => !prev)}
                >
                    <h5 className="mb-0 fs-4">{nestedBundleName}</h5>
                    <div className="d-flex align-items-center ms-auto">
                        <PriceDisplay
                            currentPrices={currentPrices}
                            formattedDuration={formattedDuration}
                            className="me-3 text-muted fw-bold fs-5"
                        />
                        <small>
                            <em className="icon-arrow_up ms-2"></em>
                        </small>
                    </div>
                </button>
            </h2>
            <div id={collapseId} className={`accordion-collapse collapse ${isOpen ? "show" : ""}`}>
                <div className="accordion-body p-3 d-grid gap-1">
                    {isSelected && <Commitment configItem={nestedBundle.items[0].configItem}/>}
                    <ConfigItemGroup variant={variant} mode="included"
                                     configItems={nestedBundle.items[0].includedItems || []} isSubBundle/>
                    <ConfigItemGroup variant={variant} mode="optional"
                                     configItems={nestedBundle.items[0].optionalItems || []} isSubBundle/>
                    <NestedBundles variant={variant} bundles={nestedIncluded} isOptional={false} onToggle={onToggle}/>
                    <NestedBundles variant={variant} bundles={nestedOptional} isOptional={true} onToggle={onToggle}/>
                </div>
            </div>
        </div>
    );
};

const NestedBundles = ({bundles, isOptional, onToggle, variant = "setup"}) => {
    if (!bundles || bundles.length === 0) return null;

    return (
        <div className="card included-card sub-bundle">
            <div className="card-body p-0">
                <div className="accordion accordion-sm d-grid gap-2" id="accordionNested">
                    {bundles
                        .sort((a, b) => a.offeringName.localeCompare(b.offeringName))
                        .map((nestedBundle, nestedIndex) => {
                            const upperLimit = nestedBundle.upperLimit;

                            if (upperLimit > 1) {
                                return (
                                    <MultiPurchaseNestedBundle
                                        variant={variant}
                                        key={nestedBundle.items[0]?.configItem?.id || nestedIndex}
                                        nestedBundle={nestedBundle}
                                        nestedIndex={nestedIndex}
                                        isOptional={isOptional}
                                        onToggle={onToggle}
                                        displayName={nestedBundle.offeringName}
                                        upperLimit={upperLimit}
                                    />
                                );
                            }

                            return (
                                <SingleNestedBundle
                                    variant={variant}
                                    key={nestedBundle.items[0]?.configItem?.id || nestedIndex}
                                    nestedBundle={nestedBundle}
                                    nestedIndex={nestedIndex}
                                    isOptional={isOptional}
                                    onToggle={onToggle}
                                />
                            );
                        })}
                </div>
            </div>
        </div>
    );
};

export default NestedBundles;