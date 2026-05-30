// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useConfiguration} from "../context/ConfigurationContext";
import {fetchConfiguration} from "../services/productConfigurationService";
import createRequestBodyConfigurationTerm from "../services/createRequestBodyConfigurationTerm";
import {formatDuration} from "../../../../utlis/helpers";
import {toast} from "react-toastify";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const Commitment = ({configItem}) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch} = useConfiguration();
    const {t, tNotification} = useTranslations();
    const terms = configItem?.productConfiguration?.configurationTerm || [];

    if (!terms.length || !configItem?.productConfiguration?.isSelected) {
        return null;
    }

    const handleCommitmentTermChange = async (selectedTerm) => {
        const requestBody = createRequestBodyConfigurationTerm(
            configuration.id,
            configItem.id,
            selectedTerm.duration,
            relatedParty
        );

        const result = await fetchConfiguration(
            requestBody,
            configItem?.id,
            dispatch,
            tNotification,
            {silent: true},
            onConfigurationChange
        );

        if (!result) {
            toast.error(tNotification("plan.updateCommitmentFailed"));
        }
    };

    const renderCommitmentTermOptions = () => {
        const sortedTermsByDuration = [...terms].sort(
            (a, b) => a.duration.amount - b.duration.amount
        );

        return (
            <div className="section-wrapper">
                <label className="form-label me-2 fw-medium">{t("common.commitmentTerm")}</label>
                <div className="btn-group" role="group" aria-label="Commitment term options">
                    {sortedTermsByDuration.map((term, index) => {
                        const radioId = `btnduration-${configItem.id}-${index}`;
                        const isDisabled = !term.isSelectable || !configItem.productConfiguration.isSelected;

                        return (
                            <React.Fragment key={index}>
                                <input
                                    type="radio"
                                    className="btn-check"
                                    name="commitment-term"
                                    id={radioId}
                                    autoComplete="off"
                                    checked={term.isSelected}
                                    disabled={isDisabled}
                                    onChange={() => handleCommitmentTermChange(term)}
                                />
                                <label
                                    className={`btn btn-outline-secondary size ${term.isSelected ? "bg-black text-white" : ""}`}
                                    htmlFor={radioId}
                                >
                                    {formatDuration(term.duration)}
                                </label>
                            </React.Fragment>
                        );
                    })}
                </div>
            </div>
        );
    };

    const renderTerminationWarning = () => {
        return (
            <div className="alert alert-warning alert-sm p-0 mt-2" role="alert">
              <span className="alert-icon">
                <span className="visually-hidden">{t("common.warning")}</span>
              </span>
                <p>
                    {t("plan.warnings.terminationPenalty")}
                </p>
            </div>
        );
    };

    return (
        <div className={`p-2 ${!configItem?.productConfiguration?.isSelected ? "opacity-50" : ""}`}>
            {renderCommitmentTermOptions()}
            {renderTerminationWarning()}
        </div>
    );
};

export default Commitment;