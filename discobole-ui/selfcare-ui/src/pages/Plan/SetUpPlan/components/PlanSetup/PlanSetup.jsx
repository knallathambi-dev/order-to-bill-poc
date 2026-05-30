// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import ConfigItemsTab from "../../../shared/components/ConfigItemsTab";
import processConfiguration from "../../../shared/services/processConfiguration";
import {useConfiguration} from "../../../shared/context/ConfigurationContext";

const PlanSetup = ({setIsUserAtFinalStep}) => {
    const {configuration, t} = useConfiguration();
    const {configurationStructure} = processConfiguration(configuration);
    const contract = configurationStructure.configItem;

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

    const initialTab = hasIncluded ? "included" : hasOptions ? "options" : "included";
    const [activeTab, setActiveTab] = useState(initialTab);

    useEffect(() => {
        const isLastStep = !hasOptions || activeTab === "options";
        setIsUserAtFinalStep(isLastStep);
    }, [activeTab, hasOptions, setIsUserAtFinalStep]);

    const handleTabSwitch = (tab) => setActiveTab(tab);

    const handleNextTab = () => {
        if (activeTab === "included" && hasOptions) {
            setActiveTab("options");
        }
    };

    const handlePreviousTab = () => {
        if (activeTab === "options") {
            setActiveTab("included");
        }
    };

    return (
        <>
            <nav
                className={`stepped-process stepper-wrapper ${hasIncluded && hasOptions ? "" : "single-step"}`}
                aria-label="Plan Setup Process"
            >
                <ol className="stepper">
                    {hasIncluded && (
                        <li className={`stepped-process-item ${activeTab === "included" ? "active" : ""}`}>
                            <a
                                className="stepped-process-link clickable-tab"
                                onClick={() => handleTabSwitch("included")}
                            >
                                {t("plan.sections.includedOffers")}
                            </a>
                        </li>
                    )}
                    {hasOptions && (
                        <li className={`stepped-process-item ${activeTab === "options" ? "active" : ""}`}>
                            <a
                                className="stepped-process-link clickable-tab"
                                onClick={() => handleTabSwitch("options")}
                            >
                                {t("plan.sections.optionalOffers")}
                            </a>
                        </li>
                    )}
                </ol>
            </nav>

            <div className="tab-content profile-content">
                {hasIncluded && (
                    <div
                        className={`tab-pane fade ${activeTab === "included" ? "show active" : ""}`}
                        id="included"
                    >
                        <ConfigItemsTab
                            mode="included"
                            contract={contract}
                            bundles={includedBundles}
                            directConfigItems={directIncludedConfigItems}
                            onAction={hasOptions ? handleNextTab : null}
                            actionLabel={t("actions.next")}
                        />
                    </div>
                )}

                {hasOptions && (
                    <div
                        className={`tab-pane fade ${activeTab === "options" ? "show active" : ""}`}
                        id="options"
                    >
                        <ConfigItemsTab
                            mode="optional"
                            bundles={optionalBundles}
                            directConfigItems={directOptionalConfigItems}
                            onAction={handlePreviousTab}
                            actionLabel={t("actions.back")}
                        />
                    </div>
                )}
            </div>
        </>
    );
};

export default PlanSetup;