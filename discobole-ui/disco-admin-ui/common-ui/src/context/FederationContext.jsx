// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {createContext, useContext, useMemo} from "react";

const FederationContext = createContext(null);

export const FederationProvider = ({config, children}) => {
    const value = useMemo(() => ({
        featureOrderManagement: config.featureOrderManagement ?? false,
        featureProductInventory: config.featureProductInventory ?? false,
        featureOrderOrchestration: config.featureOrderOrchestration ?? false,
    }), [config.featureOrderManagement, config.featureProductInventory, config.featureOrderOrchestration]);

    return (
        <FederationContext.Provider value={value}>
            {children}
        </FederationContext.Provider>
    );
};

export const useFederationConfig = () => {
    const context = useContext(FederationContext);
    if (!context) {
        return {
            featureOrderManagement: false,
            featureProductInventory: false,
            featureOrderOrchestration: false,
        };
    }
    return context;
};

export default FederationContext;