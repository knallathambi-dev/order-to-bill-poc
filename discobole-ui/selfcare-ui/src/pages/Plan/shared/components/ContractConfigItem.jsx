// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from 'react';
import {getFormattedSelectedDuration} from '../services/utils/configUtils';
import {getCurrentPrices} from '../../../../utlis/utils';
import Commitment from "./Commitment";
import PriceDisplay from "./PriceDisplay";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const ContractConfigItem = ({contract}) => {
    const [isOpen, setIsOpen] = useState(true);
    const contractName = contract?.productConfiguration?.productOffering?.name || '';
    const formattedDuration = getFormattedSelectedDuration(contract?.productConfiguration?.configurationTerm);
    const currentPrices = getCurrentPrices(contract?.productConfiguration?.configurationPrice);
    const {t} = useTranslations();

    if (!contract || !(contract.productConfiguration?.configurationTerm?.length > 0)) {
        return null;
    }

    return (
        <>
            <div className="accordion-item">
                <h2 className="accordion-header position-relative">
                    <button
                        className={`accordion-button included-accordion-button ${!isOpen ? 'collapsed' : ''}`}
                        type="button"
                        aria-expanded={isOpen}
                        aria-controls="contractCollapse"
                        onClick={() => setIsOpen((prev) => !prev)}
                    >
                        {t("plan.contract")}
                    </button>
                </h2>
                <div
                    id="contractCollapse"
                    className={`accordion-collapse collapse ${isOpen ? 'show' : ''}`}
                >
                    <div className="accordion-body pb-1 px-0">
                        <div className="bundle-container">
                            <div className="card included-card">
                                <div className="card-header bg-body-secondary border-0 text-secondary">
                                    <div className="d-flex justify-content-between align-items-center">
                                        <h5 className="mb-0 fs-4">{contractName}</h5>
                                        <div className="d-flex align-items-center">
                                            <PriceDisplay
                                                currentPrices={currentPrices}
                                                formattedDuration={formattedDuration}
                                            />
                                            <div className="check-circle">
                                                <em className="icon-checkbox_tick"></em>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <Commitment configItem={contract}/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <hr className="my-2"/>
        </>
    );
};

export default ContractConfigItem;