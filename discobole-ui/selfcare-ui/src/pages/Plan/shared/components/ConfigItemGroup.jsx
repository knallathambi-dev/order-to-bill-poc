// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import {
    addFirstSelectableItem,
    checkAncestorsSelection,
    countSelectedItemsByOfferingName,
} from "../services/utils/utils";
import {extractConfigItemProperties} from "../services/utils/configItemHelpers";
import {useConfigurationState} from "../hooks/useConfigurationState";
import {useCharacteristicHandlers} from "../hooks/useCharacteristicHandlers";
import {useOptionChange} from "../hooks/useOptionChange";
import {useToggleConfigurationAction} from "../../EditPlan/services/editPlanService";
import {useConfiguration} from "../context/ConfigurationContext";
import {RenderCharacteristics} from "./ConfigurationRenderers";
import Commitment from "./Commitment";
import PriceDisplay from "./PriceDisplay";
import ActionButtons from "./ActionButtons";

/*
 * variant="setup" (default) — SetUpPlan layout:
 *   - Price right-aligned with className "me-3 text-muted fw-bold fs-5"
 *   - formattedDuration shown after prices
 *   - Commitment rendered when isSelected
 *   - Check-circle always shown for non-selectable included items
 *   - Characteristics guarded by isSelected
 *
 * variant="edit" — EditPlan layout:
 *   - Price inline next to name with className "text-muted fw-bold fs-5 ms-3"
 *   - formattedDuration never shown
 *   - Commitment never rendered
 *   - Check-circle shown only when isSelected && !hasModifyAction && !canTerminate
 *   - Characteristics NOT guarded by isSelected
 *   - Multi-purchase groups have extra "sub-item" class
 */

const isSetup = (variant) => variant !== "edit";
const isIncludedMode = (mode) => mode === "included";

const ItemPrice = ({variant, isSelected, currentPrices, formattedDuration}) => {
    if (!isSelected || !currentPrices?.length) return null;

    if (isSetup(variant)) {
        return (
            <PriceDisplay
                currentPrices={currentPrices}
                formattedDuration={formattedDuration}
                className="me-3 text-muted fw-bold fs-5"
            />
        );
    }

    return (
        <PriceDisplay
            currentPrices={currentPrices}
            className="text-muted fw-bold fs-5 ms-3"
        />
    );
};

const ItemHeader = ({variant, displayName, isSelected, currentPrices, formattedDuration, children}) => {
    if (isSetup(variant)) {
        return (
            <div className="d-flex justify-content-between align-items-center">
                <h5 className="mb-0 fs-4">{displayName}</h5>
                <div className="d-flex align-items-center">
                    <ItemPrice
                        variant={variant}
                        isSelected={isSelected}
                        currentPrices={currentPrices}
                        formattedDuration={formattedDuration}
                    />
                    {children}
                </div>
            </div>
        );
    }

    return (
        <div className="d-flex justify-content-between align-items-center">
            <div className="d-flex align-items-center">
                <h5 className="mb-0 fs-4">{displayName}</h5>
                <ItemPrice
                    variant={variant}
                    isSelected={isSelected}
                    currentPrices={currentPrices}
                    formattedDuration={formattedDuration}
                />
            </div>
            {children}
        </div>
    );
};

const SelectionIndicator = ({
                                variant,
                                mode,
                                itemId,
                                isSelected,
                                isSelectable,
                                isAncestorSelected,
                                onOptionChange,
                                hasModifyAction,
                                canTerminate
                            }) => {
    if (isIncludedMode(mode)) {
        if (isSelectable) {
            return (
                <div className="form-check">
                    <input
                        className="form-check-input check-option"
                        type="checkbox"
                        id={`flexCheckDefault-${itemId}`}
                        checked={isSelected}
                        disabled={!isSelectable || !isAncestorSelected}
                        onChange={(e) => onOptionChange(itemId, e.target.checked)}
                    />
                </div>
            );
        }

        if (isSetup(variant)) {
            return (
                <div className="check-circle">
                    <em className="icon-checkbox_tick"></em>
                </div>
            );
        }

        if (isSelected && !hasModifyAction && !canTerminate) {
            return (
                <div className="check-circle">
                    <em className="icon-checkbox_tick"></em>
                </div>
            );
        }

        return null;
    }

    if (isSelectable) {
        return (
            <div className="form-check">
                <input
                    className="form-check-input check-option"
                    type="checkbox"
                    id={`flexCheckDefault-${itemId}`}
                    checked={isSelected}
                    disabled={!isSelectable || !isAncestorSelected}
                    onChange={(e) => onOptionChange(itemId, e.target.checked)}
                />
            </div>
        );
    }

    return null;
};

const MultiPurchaseSelectionIndicator = ({
                                             variant,
                                             mode,
                                             itemId,
                                             isSelected,
                                             isSelectable,
                                             isAncestorSelected,
                                             onOptionChange,
                                             hasModifyAction,
                                             canTerminate
                                         }) => {
    if (isIncludedMode(mode)) {
        if (isSelectable) {
            return (
                <div className="form-check">
                    <input
                        className="form-check-input check-option"
                        type="checkbox"
                        id={`flexCheckDefault-${itemId}`}
                        checked={isSelected}
                        disabled={!isSelectable || !isAncestorSelected}
                        onChange={(e) => onOptionChange(itemId, e.target.checked)}
                    />
                </div>
            );
        }

        if (isSetup(variant)) {
            return (
                <div className="check-circle">
                    <em className="icon-checkbox_tick"></em>
                </div>
            );
        }

        if (isSelected && !hasModifyAction && !canTerminate) {
            return (
                <div className="check-circle">
                    <em className="icon-checkbox_tick"></em>
                </div>
            );
        }

        return null;
    }

    if (isSelectable) {
        return (
            <div className="form-check">
                <input
                    className="form-check-input check-option"
                    type="checkbox"
                    id={`flexCheck-${itemId}`}
                    checked={isSelected}
                    disabled={!isSelectable || !isAncestorSelected}
                    onChange={(e) => onOptionChange(itemId, e.target.checked)}
                />
            </div>
        );
    }

    return null;
};

const CharacteristicsSection = ({
                                    variant,
                                    mode,
                                    itemId,
                                    isSelected,
                                    productConfiguration,
                                    activeAction,
                                    showCharacteristics,
                                    hasModifyAction,
                                    isModifying,
                                    isTerminating,
                                    radioSelection,
                                    handleRangeChange,
                                    handleRangeChangeEnd,
                                    handleCharacteristicUpdate,
                                    isSubBundle,
                                }) => {
    if (!showCharacteristics) return null;

    const isSelectedGuardMet = isSetup(variant) ? isSelected : true;

    const shouldRender = isSelectedGuardMet &&
        (!hasModifyAction || !!isModifying) &&
        !isTerminating;

    if (isIncludedMode(mode) || isSubBundle) {
        if (!shouldRender) return null;
        return (
            <div className="card-body p-3 d-grid gap-2">
                <RenderCharacteristics
                    productConfiguration={productConfiguration}
                    configurationItemId={itemId}
                    actionType={activeAction}
                    radioSelection={radioSelection}
                    checkAncestorsSelection={checkAncestorsSelection}
                    handleRangeChange={handleRangeChange}
                    handleRangeChangeEnd={handleRangeChangeEnd}
                    handleCharacteristicUpdate={handleCharacteristicUpdate}
                />
            </div>
        );
    }

    return (
        <div
            id={`collapse-${itemId}`}
            className="accordion-collapse collapse show"
            aria-labelledby={`heading-${itemId}`}
        >
            {isSetup(variant) && isSelected && (
                <Commitment configItem={{id: itemId, productConfiguration}}/>
            )}
            {shouldRender && (
                <div className="card-body p-3 d-grid gap-2">
                    <RenderCharacteristics
                        productConfiguration={productConfiguration}
                        configurationItemId={itemId}
                        actionType={activeAction}
                        radioSelection={radioSelection}
                        checkAncestorsSelection={checkAncestorsSelection}
                        handleRangeChange={handleRangeChange}
                        handleRangeChangeEnd={handleRangeChangeEnd}
                        handleCharacteristicUpdate={handleCharacteristicUpdate}
                    />
                </div>
            )}
        </div>
    );
};

const SingleItem = ({mode, variant, group, isSubBundle, handlers}) => {
    const {configuration} = useConfiguration();
    const {productConfiguration, id} = group.items[0];
    const displayName = group.offeringName;
    const props = extractConfigItemProperties(productConfiguration);
    const isAncestorSelected = checkAncestorsSelection(id, configuration.computedProductConfigurationItem);

    const {
        radioSelection,
        openModifySections,
        openTerminateSections,
        toggleTermination,
        toggleModify,
        handleOptionChange,
        handleRangeChange,
        handleRangeChangeEnd,
        handleCharacteristicUpdate,
    } = handlers;

    const characteristicsSection = (
        <CharacteristicsSection
            variant={variant}
            mode={mode}
            itemId={id}
            isSelected={props.isSelected}
            productConfiguration={productConfiguration}
            activeAction={props.activeAction}
            showCharacteristics={props.showCharacteristics}
            hasModifyAction={props.hasModifyAction}
            isModifying={openModifySections[id]}
            isTerminating={openTerminateSections[id]}
            radioSelection={radioSelection}
            handleRangeChange={handleRangeChange}
            handleRangeChangeEnd={handleRangeChangeEnd}
            handleCharacteristicUpdate={handleCharacteristicUpdate}
            isSubBundle={isSubBundle}
        />
    );

    const actionButtonsAndIndicator = (
        <div className="d-grid gap-2 d-flex justify-content-end align-items-center">
            <ActionButtons
                itemId={id}
                canTerminate={props.canTerminate}
                hasModifyAction={props.hasModifyAction}
                isTerminating={openTerminateSections[id]}
                isModifying={openModifySections[id]}
                onTerminate={toggleTermination}
                onModify={toggleModify}
            />
            <SelectionIndicator
                variant={variant}
                mode={mode}
                itemId={id}
                isSelected={props.isSelected}
                isSelectable={props.isSelectable}
                isAncestorSelected={isAncestorSelected}
                onOptionChange={handleOptionChange}
                hasModifyAction={props.hasModifyAction}
                canTerminate={props.canTerminate}
            />
        </div>
    );

    if (isSubBundle) {
        const outerClass = isIncludedMode(mode)
            ? `card included-card ${!isAncestorSelected ? "opacity-50" : ""}`
            : `${!isAncestorSelected ? "opacity-50" : ""}`;

        return (
            <div key={id} className={outerClass}>
                <div className="card">
                    <div className="card-body">
                        <ItemHeader
                            variant={variant}
                            displayName={displayName}
                            isSelected={props.isSelected}
                            currentPrices={props.currentPrices}
                            formattedDuration={props.formattedDuration}
                        >
                            {actionButtonsAndIndicator}
                        </ItemHeader>
                    </div>
                </div>
                {isSetup(variant) && props.isSelected && (
                    <Commitment configItem={group.items[0]}/>
                )}
                {characteristicsSection}
            </div>
        );
    }

    if (isIncludedMode(mode)) {
        return (
            <div
                key={id}
                className={`card included-card ${!isAncestorSelected ? "opacity-50" : ""}`}
            >
                <div className="card-header bg-body-secondary border-0 text-secondary">
                    <ItemHeader
                        variant={variant}
                        displayName={displayName}
                        isSelected={props.isSelected}
                        currentPrices={props.currentPrices}
                        formattedDuration={props.formattedDuration}
                    >
                        {actionButtonsAndIndicator}
                    </ItemHeader>
                </div>
                {isSetup(variant) && props.isSelected && (
                    <Commitment configItem={group.items[0]}/>
                )}
                {characteristicsSection}
            </div>
        );
    }

    return (
        <div
            key={id}
            className={`accordion-item setup-item border-0 ${!isAncestorSelected ? "opacity-50" : ""}`}
        >
            <div className="accordion-header border-0" id={`heading-${id}`}>
                <div
                    className="accordion-button bg-body-secondary p-2 ps-3"
                    style={{cursor: 'default'}}
                >
                    <div className="d-flex justify-content-between align-items-center w-100">
                        {isSetup(variant) ? (
                            <>
                                <h5 className="mb-0 fs-4">{displayName}</h5>
                                <div className="d-flex align-items-center">
                                    <ItemPrice
                                        variant={variant}
                                        isSelected={props.isSelected}
                                        currentPrices={props.currentPrices}
                                        formattedDuration={props.formattedDuration}
                                    />
                                    {actionButtonsAndIndicator}
                                </div>
                            </>
                        ) : (
                            <>
                                <div className="d-flex align-items-center">
                                    <h5 className="mb-0 fs-4">{displayName}</h5>
                                    <ItemPrice
                                        variant={variant}
                                        isSelected={props.isSelected}
                                        currentPrices={props.currentPrices}
                                        formattedDuration={props.formattedDuration}
                                    />
                                </div>
                                {actionButtonsAndIndicator}
                            </>
                        )}
                    </div>
                </div>
            </div>
            {characteristicsSection}
        </div>
    );
};

const MultiPurchaseItemCard = ({mode, variant, groupItem, handlers}) => {
    const {configuration} = useConfiguration();
    const itemProps = extractConfigItemProperties(groupItem.productConfiguration);
    const displayName = groupItem.productConfiguration?.productOffering?.name;
    const isAncestorSelected = checkAncestorsSelection(
        groupItem.id,
        configuration.computedProductConfigurationItem
    );

    const {
        radioSelection,
        openModifySections,
        openTerminateSections,
        toggleTermination,
        toggleModify,
        handleOptionChange,
        handleRangeChange,
        handleRangeChangeEnd,
        handleCharacteristicUpdate,
    } = handlers;

    const isSelectedGuardMet = isSetup(variant) ? itemProps.isSelected : true;
    const shouldRenderCharacteristics = itemProps.showCharacteristics &&
        isSelectedGuardMet &&
        (!itemProps.hasModifyAction || !!openModifySections[groupItem.id]) &&
        !openTerminateSections[groupItem.id];

    return (
        <React.Fragment key={groupItem.id}>
            <div className="card included-card border border-light mt-2">
                <div className="card-body">
                    <ItemHeader
                        variant={variant}
                        displayName={displayName}
                        isSelected={itemProps.isSelected}
                        currentPrices={itemProps.currentPrices}
                        formattedDuration={itemProps.formattedDuration}
                    >
                        <div className="d-grid gap-2 d-flex justify-content-end align-items-center">
                            <ActionButtons
                                itemId={groupItem.id}
                                canTerminate={itemProps.canTerminate}
                                hasModifyAction={itemProps.hasModifyAction}
                                isTerminating={openTerminateSections[groupItem.id]}
                                isModifying={openModifySections[groupItem.id]}
                                onTerminate={toggleTermination}
                                onModify={toggleModify}
                            />
                            <MultiPurchaseSelectionIndicator
                                variant={variant}
                                mode={mode}
                                itemId={groupItem.id}
                                isSelected={itemProps.isSelected}
                                isSelectable={itemProps.isSelectable}
                                isAncestorSelected={isAncestorSelected}
                                onOptionChange={handleOptionChange}
                                hasModifyAction={itemProps.hasModifyAction}
                                canTerminate={itemProps.canTerminate}
                            />
                        </div>
                    </ItemHeader>
                </div>
            </div>
            {isSetup(variant) && itemProps.isSelected && (
                <Commitment configItem={groupItem}/>
            )}
            {shouldRenderCharacteristics && (
                <div className="card-body p-3 d-grid gap-2">
                    <RenderCharacteristics
                        productConfiguration={groupItem.productConfiguration}
                        configurationItemId={groupItem.id}
                        actionType={itemProps.activeAction}
                        radioSelection={radioSelection}
                        checkAncestorsSelection={checkAncestorsSelection}
                        handleRangeChange={handleRangeChange}
                        handleRangeChangeEnd={handleRangeChangeEnd}
                        handleCharacteristicUpdate={handleCharacteristicUpdate}
                    />
                </div>
            )}
        </React.Fragment>
    );
};

const MultiPurchaseGroup = ({mode, variant, group, isSubBundle, handlers}) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, t, tNotification} = useConfiguration();
    const [isOpen, setIsOpen] = useState(true);
    const displayName = group.offeringName;
    const upperLimit = group.upperLimit;
    const selectedItemCount = countSelectedItemsByOfferingName(
        configuration, group.offeringName, group.items[0].id
    );
    const isAncestorSelected = checkAncestorsSelection(
        group.items[0].id,
        configuration.computedProductConfigurationItem
    );
    const isDisabled =
        group.items.filter((item) => item.productConfiguration.isSelected).length === upperLimit ||
        !isAncestorSelected;

    const stableGroupId = group.items[0]?.id || group.offeringName;
    const headingId = `heading-${stableGroupId}`;
    const collapseId = `collapse-${stableGroupId}`;

    const extraClass = isSetup(variant) ? "" : " sub-item";
    const accordionButtonClass = isSubBundle
        ? `accordion-button border border-light p-2 ps-3 pe-0 ${!isOpen ? 'collapsed' : ''}`
        : `accordion-button bg-body-secondary p-2 ps-3 pe-0 ${!isOpen ? 'collapsed' : ''}`;

    return (
        <div
            key={stableGroupId}
            className={`accordion-item setup-item${extraClass} border-0 ${!isAncestorSelected ? "opacity-50" : ""}`}
        >
            <div className="accordion-header border-0" id={headingId}>
                <button
                    className={accordionButtonClass}
                    type="button"
                    aria-expanded={isOpen}
                    aria-controls={collapseId}
                    onClick={() => setIsOpen((prev) => !prev)}
                >
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
                                className={`add-multiple-button ${isDisabled ? 'disabled' : ''}`}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    addFirstSelectableItem(
                                        group.items, false,
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
            </div>
            <div id={collapseId} className={`accordion-collapse collapse ${isOpen ? 'show' : ''}`}
                 aria-labelledby={headingId}>
                <div className="accordion-body p-2">
                    {group.items
                        .filter((groupItem) => groupItem.productConfiguration.isSelected)
                        .map((groupItem) => (
                            <MultiPurchaseItemCard
                                key={groupItem.id}
                                mode={mode}
                                variant={variant}
                                groupItem={groupItem}
                                handlers={handlers}
                            />
                        ))}
                </div>
            </div>
        </div>
    );
};

const ConfigItemGroup = ({
                             mode = "included",
                             variant = "setup",
                             configItems = [],
                             isSubBundle = false,
                         }) => {
    const {configuration, relatedParty, dispatch} = useConfiguration();
    const [openModifySections, setOpenModifySections] = useState({});
    const [openTerminateSections, setOpenTerminateSections] = useState({});

    const {
        selectedConfigItems,
        radioSelection,
        setSelectedConfigItems,
        setRadioSelection,
    } = useConfigurationState(configItems);

    const {handleRangeChange, handleRangeChangeEnd, handleCharacteristicUpdate} =
        useCharacteristicHandlers(
            selectedConfigItems,
            setSelectedConfigItems,
            radioSelection,
            setRadioSelection,
        );

    const toggleTermination = useToggleConfigurationAction(
        openTerminateSections,
        setOpenTerminateSections,
        configuration.id,
        relatedParty,
        dispatch,
        true
    );

    const toggleModify = useToggleConfigurationAction(
        openModifySections,
        setOpenModifySections,
        configuration.id,
        relatedParty,
        dispatch,
        false
    );

    const handleOptionChange = useOptionChange(setSelectedConfigItems);

    const handlers = {
        radioSelection,
        openModifySections,
        openTerminateSections,
        toggleTermination,
        toggleModify,
        handleOptionChange,
        handleRangeChange,
        handleRangeChangeEnd,
        handleCharacteristicUpdate,
    };

    if (!configItems.length) return null;

    return (
        <>
            {configItems
                .sort((a, b) => a.offeringName.localeCompare(b.offeringName))
                .map((group) => {
                    const upperLimit = group.upperLimit;

                    if (upperLimit <= 1) {
                        return (
                            <SingleItem
                                key={group.items[0]?.id || group.offeringName}
                                mode={mode}
                                variant={variant}
                                group={group}
                                isSubBundle={isSubBundle}
                                handlers={handlers}
                            />
                        );
                    }

                    return (
                        <MultiPurchaseGroup
                            key={group.items[0]?.id || group.offeringName}
                            mode={mode}
                            variant={variant}
                            group={group}
                            isSubBundle={isSubBundle}
                            handlers={handlers}
                        />
                    );
                })}
        </>
    );
};

export default ConfigItemGroup;