// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from 'react';
import NestedBundles from "../../../shared/components/NestedBundles";
import ConfigItemGroup from "../../../shared/components/ConfigItemGroup";
import PriceDisplay from "../../../shared/components/PriceDisplay";
import {addFirstSelectableItem, countSelectedItemsByOfferingName,} from "../../../shared/services/utils/utils";
import {extractConfigItemProperties} from "../../../shared/services/utils/configItemHelpers";
import {
    getProductConfiguration,
    performActionAndGetCharacteristics,
} from "../../../shared/services/productConfigurationService";
import processConfiguration from "../../../shared/services/processConfiguration";
import {useConfiguration} from "../../../shared/context/ConfigurationContext";
import {getCurrentPrices} from "../../../../../utlis/utils";
import {useToggleConfigurationAction} from "../../services/editPlanService";
import {Modal} from "../../../../../components";
import TerminateBundleModal from "./Modals/TerminateBundleModal";
import useTranslations from "../../../../../utlis/i18n/useTranslations";
import {toast} from "react-toastify";

const MultiPurchaseGroupItem = ({
                                    groupItem,
                                    idx,
                                    handleToggle,
                                    openTerminateSections,
                                    toggleTermination,
                                }) => {
    const {t} = useTranslations();
    const bundleConfigId = groupItem.configItem?.id || `group-${idx}`;
    const bundleName = groupItem.configItem?.productConfiguration?.productOffering?.name;
    const {isSelected, isSelectable, canTerminate} = extractConfigItemProperties(
        groupItem.configItem?.productConfiguration
    );

    const nestedIncludedBundles = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle")
    ) || [];
    const nestedOptionalBundles = groupItem?.nestedBundles?.filter(
        (nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle")
    ) || [];

    return (
        <div className="card" key={bundleConfigId}>
            <div className="card-body py-3">
                <div className="accordion-item">
                    <h4 className="accordion-header position-relative">
                        <div className="accordion-button optional-accordion-button">
                            <div
                                className="d-flex justify-content-between align-items-center w-100 optional-multi-item">
                                <h5 className="mb-0 fs-5">
                                    {bundleName}
                                    <small className="text-muted">
                                        {" "}( {t("common.selectToOrder")} )
                                    </small>
                                </h5>
                                <div className="d-grid gap-2 d-flex justify-content-end align-items-center">
                                    {canTerminate && (
                                        <button
                                            type="button"
                                            className="btn btn-danger btn-sm action-btn terminate"
                                            onClick={() => toggleTermination(bundleConfigId, "terminate")}
                                            aria-expanded={!!openTerminateSections[bundleConfigId]}
                                        >
                                            <em className="icon-delete me-1"></em>
                                            {openTerminateSections[bundleConfigId]
                                                ? t("actions.cancelTerminate")
                                                : t("actions.terminate")}
                                        </button>
                                    )}
                                    {!canTerminate && (
                                        isSelectable ? (
                                            <input
                                                className="form-check-input check-option"
                                                type="checkbox"
                                                id={`checkboxOptionalBundle-${bundleConfigId}`}
                                                disabled={!isSelectable}
                                                checked={isSelected}
                                                onChange={() => handleToggle(bundleConfigId, isSelected)}
                                            />
                                        ) : (
                                            isSelected && (
                                                <div className="check-circle">
                                                    <em className="icon-checkbox_tick"></em>
                                                </div>
                                            )
                                        )
                                    )}
                                </div>
                            </div>
                        </div>
                    </h4>
                    <div
                        id={`collapseSubitemOne-${bundleConfigId}`}
                        className="accordion-collapse collapse show"
                    >
                        <div className="accordion-body p-2">
                            <div className="bundle-item-wrapper">
                                <div className="bundle-container">
                                    <div className="d-grid gap-2">
                                        <ConfigItemGroup
                                            variant="edit"
                                            mode="included"
                                            configItems={groupItem.includedItems || []}
                                            isSubBundle={false}
                                        />
                                        <ConfigItemGroup
                                            variant="edit"
                                            mode="optional"
                                            configItems={groupItem.optionalItems || []}
                                            isSubBundle={false}
                                        />
                                        <NestedBundles
                                            variant="edit"
                                            bundles={nestedIncludedBundles}
                                            isOptional={false}
                                            onToggle={handleToggle}
                                        />
                                        <NestedBundles
                                            variant="edit"
                                            bundles={nestedOptionalBundles}
                                            isOptional={true}
                                            onToggle={handleToggle}
                                        />
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

const MultiPurchaseBundle = ({
                                 bundle,
                                 handleToggle,
                                 handleTerminateButtonClick,
                                 openTerminateSections,
                                 toggleTermination
                             }) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, tNotification} = useConfiguration();
    const {t} = useTranslations();
    const displayName = bundle.offeringName;
    const upperLimit = bundle.upperLimit;
    const selectedItemCount = countSelectedItemsByOfferingName(
        configuration, bundle.offeringName, bundle.items[0].configItem.id
    );
    const isDisabled =
        bundle.items.filter((item) => item.configItem.productConfiguration.isSelected).length === upperLimit;

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
                                {selectedItemCount}/{upperLimit}
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
            <div className="d-flex flex-column gap-3">
                {bundle.items
                    .filter((groupItem) => groupItem.configItem.productConfiguration.isSelected)
                    .map((groupItem, idx) => (
                        <MultiPurchaseGroupItem
                            key={groupItem.configItem?.id || `group-${idx}`}
                            groupItem={groupItem}
                            idx={idx}
                            handleToggle={handleToggle}
                            handleTerminateButtonClick={handleTerminateButtonClick}
                            openTerminateSections={openTerminateSections}
                            toggleTermination={toggleTermination}
                        />
                    ))}
            </div>
        </div>
    );
};

const SingleBundle = ({bundle, handleToggle, handleTerminateButtonClick, openTerminateSections}) => {
    const {t} = useTranslations();

    const bundleConfigId = bundle.items[0].configItem?.id;
    const bundleName = bundle.items[0].configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(bundle.items[0].configItem?.productConfiguration.configurationPrice);
    const {isSelected, isSelectable, canTerminate} = extractConfigItemProperties(
        bundle.items[0].configItem?.productConfiguration
    );

    const nestedIncludedBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle"));
    const nestedOptionalBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle"));

    return (
        <div className="mb-4">
            <div className="d-flex align-items-center mb-2">
                <h6 className="mb-0">{bundleName}</h6>
                {isSelected && (
                    <PriceDisplay
                        currentPrices={currentPrices}
                        className="text-muted fw-bold fs-5 ms-3"
                    />
                )}
            </div>
            <div className="d-grid gap-2 d-flex justify-content-end align-items-center">
                {canTerminate && (
                    <button
                        type="button"
                        className="btn btn-danger btn-sm action-btn terminate"
                        onClick={() => handleTerminateButtonClick(bundleConfigId, bundleName)}
                        aria-expanded={!!openTerminateSections[bundleConfigId]}
                    >
                        <em className="icon-delete me-1"></em>
                        {openTerminateSections[bundleConfigId]
                            ? t("actions.cancelTerminate")
                            : t("actions.terminate")}
                    </button>
                )}
                {isSelectable && !canTerminate && (
                    <input
                        className="form-check-input check-option"
                        type="checkbox"
                        id={`checkboxOptionalBundle-${bundleConfigId}`}
                        disabled={!isSelectable}
                        checked={isSelected}
                        onChange={() => handleToggle(bundleConfigId, isSelected)}
                    />
                )}
            </div>
            <div className="d-grid gap-2">
                <ConfigItemGroup
                    variant="edit"
                    mode="included"
                    configItems={bundle.items[0].includedItems || []}
                    isSubBundle={false}
                />
                <ConfigItemGroup
                    variant="edit"
                    mode="optional"
                    configItems={bundle.items[0].optionalItems || []}
                    isSubBundle={false}
                />
                <NestedBundles
                    variant="edit"
                    bundles={nestedIncludedBundles}
                    isOptional={false}
                    onToggle={handleToggle}
                />
                <NestedBundles
                    variant="edit"
                    bundles={nestedOptionalBundles}
                    isOptional={true}
                    onToggle={handleToggle}
                />
            </div>
        </div>
    );
};

const OptionalSingleBundle = ({bundle, handleToggle, handleTerminateButtonClick, openTerminateSections}) => {
    const {t} = useTranslations();

    const bundleConfigId = bundle.items[0].configItem?.id;
    const bundleName = bundle.items[0].configItem?.productConfiguration?.productOffering?.name;
    const currentPrices = getCurrentPrices(bundle.items[0].configItem?.productConfiguration.configurationPrice);
    const {isSelected, isSelectable, canTerminate} = extractConfigItemProperties(
        bundle.items[0].configItem?.productConfiguration
    );

    const nestedIncludedBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "includedBundle"));
    const nestedOptionalBundles = bundle.items
        .flatMap((item) => item.nestedBundles || [])
        .filter((nb) => nb?.items?.some((ci) => ci.bundleType === "optionalBundle"));

    return (
        <div className="mt-3 mb-4">
            <div className="d-flex justify-content-between align-items-center position-relative mb-2">
                <div className="d-flex align-items-center">
                    <h6 className="mb-0">{bundleName}</h6>
                    {isSelected && (
                        <PriceDisplay
                            currentPrices={currentPrices}
                            className="text-muted fw-bold fs-5 ms-3"
                        />
                    )}
                </div>
                <div className="d-grid gap-2 d-flex justify-content-end align-items-center">
                    {canTerminate && (
                        <button
                            type="button"
                            className="btn btn-danger btn-sm action-btn terminate"
                            onClick={() => handleTerminateButtonClick(bundleConfigId, bundleName)}
                            aria-expanded={!!openTerminateSections[bundleConfigId]}
                        >
                            <em className="icon-delete me-1"></em>
                            {openTerminateSections[bundleConfigId]
                                ? t("actions.cancelTerminate")
                                : t("actions.terminate")}
                        </button>
                    )}
                    {isSelectable && !canTerminate && (
                        <input
                            className="form-check-input check-option"
                            type="checkbox"
                            id={`checkboxOptionalBundle-${bundleConfigId}`}
                            disabled={!isSelectable}
                            checked={isSelected}
                            onChange={() => handleToggle(bundleConfigId, isSelected)}
                        />
                    )}
                </div>
            </div>
            <div className="d-grid gap-2">
                <ConfigItemGroup
                    variant="edit"
                    mode="included"
                    configItems={bundle.items[0].includedItems || []}
                    isSubBundle={false}
                />
                <ConfigItemGroup
                    variant="edit"
                    mode="optional"
                    configItems={bundle.items[0].optionalItems || []}
                    isSubBundle={false}
                />
                <NestedBundles
                    variant="edit"
                    bundles={nestedIncludedBundles}
                    isOptional={false}
                    onToggle={handleToggle}
                />
                <NestedBundles
                    variant="edit"
                    bundles={nestedOptionalBundles}
                    isOptional={true}
                    onToggle={handleToggle}
                />
            </div>
        </div>
    );
};

const BundleList = ({
                        bundles,
                        isOptionalSection,
                        handleToggle,
                        handleTerminateButtonClick,
                        openTerminateSections,
                        toggleTermination
                    }) => {
    if (!bundles.length) return null;

    const sorted = [...bundles].sort((a, b) =>
        a.offeringName.toLowerCase().localeCompare(b.offeringName.toLowerCase())
    );

    return sorted.map((bundle) => {
        if (bundle.upperLimit > 1) {
            return (
                <MultiPurchaseBundle
                    key={`bundle-${bundle.id}`}
                    bundle={bundle}
                    handleToggle={handleToggle}
                    handleTerminateButtonClick={handleTerminateButtonClick}
                    openTerminateSections={openTerminateSections}
                    toggleTermination={toggleTermination}
                />
            );
        }

        if (isOptionalSection) {
            return (
                <OptionalSingleBundle
                    key={`bundle-${bundle.items[0].configItem?.id}`}
                    bundle={bundle}
                    handleToggle={handleToggle}
                    handleTerminateButtonClick={handleTerminateButtonClick}
                    openTerminateSections={openTerminateSections}
                />
            );
        }

        return (
            <SingleBundle
                key={`bundle-${bundle.items[0].configItem?.id}`}
                bundle={bundle}
                handleToggle={handleToggle}
                handleTerminateButtonClick={handleTerminateButtonClick}
                openTerminateSections={openTerminateSections}
            />
        );
    });
};

const PlanEditor = () => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, tNotification} = useConfiguration();
    const {t} = useTranslations();
    const {configurationStructure} = processConfiguration(configuration);

    const [openTerminateSections, setOpenTerminateSections] = useState({});
    const [selectedBundleName, setSelectedBundleName] = useState('');
    const [selectedItems, setSelectedItems] = useState([]);
    const [selectedBundleId, setSelectedBundleId] = useState('');
    const [isTerminateBundleModalVisible, setIsTerminateBundleModalVisible] = useState(false);

    const includedBundles = configurationStructure.nestedBundles.filter((bundle) =>
        bundle?.items.some((item) => item?.bundleType === "includedBundle")
    );
    const optionalBundles = configurationStructure.nestedBundles.filter((bundle) =>
        bundle?.items.some((item) => item?.bundleType === "optionalBundle")
    );
    const directIncludedConfigItems = configurationStructure.includedItems;
    const directOptionalConfigItems = configurationStructure.optionalItems;

    const hasIncluded = includedBundles.length > 0 || directIncludedConfigItems.length > 0;
    const hasOptions = optionalBundles.length > 0 || directOptionalConfigItems.length > 0;

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

    const toggleTermination = useToggleConfigurationAction(
        openTerminateSections,
        setOpenTerminateSections,
        configuration.id,
        relatedParty,
        dispatch
    );

    const openTerminateBundleModal = async (bundleId, bundleDisplayName) => {
        setSelectedBundleId(bundleId);
        setSelectedBundleName(bundleDisplayName);

        const productConfigData = await getProductConfiguration(configuration.id, dispatch, tNotification);

        if (!productConfigData) {
            toast.error(tNotification("common.fetchConfigurationFailed"));
            return;
        }

        const targetConfigItem = productConfigData.computedProductConfigurationItem.find(
            (item) => item.id === bundleId
        );
        if (!targetConfigItem || !targetConfigItem.productConfigurationItemRelationship) return;

        const findConfigItemById = (id) =>
            productConfigData.computedProductConfigurationItem.find((item) => item.id === id);

        const hasTerminateActionInConfigItem = (configItem) => {
            if (!configItem?.productConfiguration?.configurationAction) return false;
            return configItem.productConfiguration.configurationAction.some(
                (action) => action.action === 'terminate' && action.isSelected === true
            );
        };

        const getTerminateRelatedItems = (configItem) => {
            const relatedItemsWithTerminate = [];
            if (!configItem?.productConfigurationItemRelationship) return relatedItemsWithTerminate;

            const bundleRelations = configItem.productConfigurationItemRelationship.filter(
                (relation) => relation.relationshipType === 'bundles'
            );
            if (!bundleRelations?.length) return relatedItemsWithTerminate;

            bundleRelations.forEach((relation) => {
                const relatedConfigItem = findConfigItemById(relation.id);
                if (relatedConfigItem) {
                    if (hasTerminateActionInConfigItem(relatedConfigItem)) {
                        relatedItemsWithTerminate.push(relatedConfigItem);
                    }
                    const nestedItems = getTerminateRelatedItems(relatedConfigItem) || [];
                    relatedItemsWithTerminate.push(
                        ...nestedItems.filter((item) => hasTerminateActionInConfigItem(item))
                    );
                }
            });

            return relatedItemsWithTerminate;
        };

        const relatedItemsToTerminate = getTerminateRelatedItems(targetConfigItem);
        const productOfferingsToTerminate = relatedItemsToTerminate
            .filter(
                (item) =>
                    item?.productConfiguration?.productOffering &&
                    item?.productConfiguration?.isVisible
            )
            .map((item) => item.productConfiguration.productOffering);

        setSelectedItems(productOfferingsToTerminate);
    };

    const handleTerminateButtonClick = (bundleId, bundleDisplayName) => {
        const isCurrentlyTerminating = !openTerminateSections[bundleId];

        if (isCurrentlyTerminating) {
            toggleTermination(bundleId, "terminate").then(() => {
                openTerminateBundleModal(bundleId, bundleDisplayName);
                setIsTerminateBundleModalVisible(true);
            });
        } else {
            toggleTermination(bundleId, "terminate");
        }
    };

    const handleModalClose = () => {
        toggleTermination(selectedBundleId, "terminate");
        setIsTerminateBundleModalVisible(false);
    };

    const handleModalConfirm = () => {
        setIsTerminateBundleModalVisible(false);
    };

    const sharedBundleProps = {
        handleToggle: handleBundleToggle,
        handleTerminateButtonClick,
        openTerminateSections,
        toggleTermination,
    };

    return (
        <>
            <h5>{t("plan.myPlanDetails")}</h5>
            <div className="included mt-3">
                <div className="row">
                    <div className="col-md-12">
                        {hasIncluded && (
                            <>
                                <h3 className="text-primary mb-2">
                                    {t("plan.planPreview.included")}
                                </h3>
                                <p>{t("plan.modify.includedOptions")}</p>

                                <BundleList
                                    bundles={includedBundles}
                                    isOptionalSection={false}
                                    {...sharedBundleProps}
                                />

                                {directIncludedConfigItems.length > 0 && (
                                    <div className="mt-3 mb-4">
                                        <h6 className="mb-2">
                                            {t("plan.sections.includedOffers")}
                                        </h6>
                                        <div className="d-grid gap-2">
                                            <ConfigItemGroup
                                                variant="edit"
                                                mode="included"
                                                configItems={directIncludedConfigItems}
                                                isSubBundle={false}
                                            />
                                        </div>
                                    </div>
                                )}
                            </>
                        )}

                        {hasIncluded && hasOptions && <hr className="my-4"/>}

                        {hasOptions && (
                            <div className="options">
                                <div className="row">
                                    <div className="col-md-12">
                                        <h3 className="text-primary mb-2">
                                            {t("plan.planPreview.options")}
                                        </h3>

                                        <BundleList
                                            bundles={optionalBundles}
                                            isOptionalSection={true}
                                            {...sharedBundleProps}
                                        />

                                        {directOptionalConfigItems.length > 0 && (
                                            <div className="mt-3 mb-4">
                                                <h6 className="mb-2">
                                                    {t("plan.sections.optionalOffers")}
                                                </h6>
                                                <div className="d-grid gap-2">
                                                    <ConfigItemGroup
                                                        variant="edit"
                                                        mode="optional"
                                                        configItems={directOptionalConfigItems}
                                                        isSubBundle={false}
                                                    />
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
                {isTerminateBundleModalVisible && (
                    <Modal
                        show={isTerminateBundleModalVisible}
                        title={selectedBundleName}
                        body={
                            <TerminateBundleModal
                                items={selectedItems}
                                onConfirm={handleModalConfirm}
                                hideModal={handleModalClose}
                            />
                        }
                        onClose={handleModalClose}
                        hideFooter={true}
                    />
                )}
            </div>
        </>
    );
};

export default PlanEditor;