// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {createContext, useContext, useMemo} from "react";
import BffAuthService from "../service/BffAuthService";
import useNavigationGuard from "../hooks/useNavigationGuard";
import {ReloadButton} from "../components/ReloadButton";

export const createAuthContext = (entitlementMap) => {
    const AuthContext = createContext(null);

    const AuthProvider = ({children}) => {
        const value = useMemo(() => {
            const auth = {
                _entitlementsLoaded: BffAuthService.entitlementsLoaded,
                _entitlementsFailed: BffAuthService.entitlementsFailed,
            };
            Object.entries(entitlementMap).forEach(([key, entitlement]) => {
                auth[key] = BffAuthService.hasEntitlement(entitlement);
            });
            return auth;
        }, []);

        return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
    };

    const useAuth = () => {
        const context = useContext(AuthContext);
        if (!context) {
            throw new Error("useAuth must be used within an AuthProvider");
        }
        return context;
    };

    const ProtectedRoute = ({requires, children}) => {
        const auth = useAuth();
        const keys = Array.isArray(requires) ? requires : [requires];
        const isAuthorized = keys.every((key) => auth[key]);

        useNavigationGuard(!isAuthorized && !auth._entitlementsFailed);

        if (auth._entitlementsFailed) {
            return (
                <div className="text-center p-5">
                    <h2 className="h4 fw-bold mb-2">Something went wrong</h2>
                    <p className="text-muted mb-3">
                        We couldn't load your role information. This is usually temporary — please refresh to try again.
                    </p>
                    <ReloadButton onClick={() => window.location.reload()}/>
                </div>
            );
        }

        return isAuthorized ? children : null;
    };

    return {AuthProvider, useAuth, ProtectedRoute};
};