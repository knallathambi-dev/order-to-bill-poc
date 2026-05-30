// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {createContext, useContext, useMemo} from "react";
import {useDispatch, useSelector} from "react-redux";
import useTranslations from "../../../../utlis/i18n/useTranslations";

const ConfigurationContext = createContext(null);

export const ConfigurationProvider = ({configuration, onConfigurationChange, children}) => {
    const dispatch = useDispatch();
    const {relatedParty} = useSelector((state) => state.auth);
    const {t, tNotification} = useTranslations();

    const value = useMemo(() => ({
        configuration,
        onConfigurationChange,
        relatedParty,
        dispatch,
        t,
        tNotification,
    }), [configuration, onConfigurationChange, relatedParty, dispatch, t, tNotification]);

    return (
        <ConfigurationContext.Provider value={value}>
            {children}
        </ConfigurationContext.Provider>
    );
};

export const useConfiguration = () => {
    const context = useContext(ConfigurationContext);
    if (!context) {
        throw new Error("useConfiguration must be used within a ConfigurationProvider");
    }
    return context;
};

export default ConfigurationContext;