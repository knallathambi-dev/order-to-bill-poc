// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {createContext, useCallback, useContext, useEffect, useRef} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {useNavigate} from 'react-router-dom';
import {setAuthStatus, setEmail, setRelatedParty} from '../store/actions/authActions';
import apiClient from '../services/api/apiClient';

const REQUIRED_USER_FIELDS = ["relatedPartyId", "username", "relatedPartyRole", "email"];

const SESSION_POLL_INTERVAL = 5 * 60 * 1000;

const AuthContext = createContext(null);

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) console.error('useAuth must be used within an AuthProvider');
    return ctx;
};

async function ensureCsrf() {
    try {
        await apiClient.get("/auth/csrf");
    } catch {
    }
}

async function loadMe(updateAuthState, clearAuthState) {
    try {
        const res = await apiClient.get("/auth/me");
        const {user, serviceSession} = res.data || {};

        if (user?.email && !serviceSession) {
            updateAuthState(res.data);
        } else {
            clearAuthState();
        }
    } catch (error) {
        if (error?.response?.status !== 401) {
            console.error("Failed to load user:", error);
        }
        clearAuthState();
    }
}

const validateUserFields = (user, isServiceSession) => {
    if (isServiceSession) return {ok: true, missing: []};
    const missing = REQUIRED_USER_FIELDS.filter((f) => !user?.[f]);
    return {ok: missing.length === 0, missing};
};

const parseApiError = (e, fallbackMessage) => {
    const data = e?.response?.data || {};
    const message = data.message || data.error || fallbackMessage;
    return {success: false, message};
};

export const AuthProvider = ({children}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const sessionExpiredHandled = useRef(false);
    const fullSessionExpiredHandled = useRef(false);
    const pollTimerRef = useRef(null);
    const isAuthenticated = useSelector((state) => state.auth?.isAuthenticated);

    const updateAuthState = useCallback((payload) => {
        const {user = {}, serviceSession = false} = payload;

        if (serviceSession || !user?.email) {
            dispatch(setAuthStatus(false));
            dispatch(setRelatedParty(null));
            dispatch(setEmail(null));
            return;
        }

        dispatch(setAuthStatus(true));

        const relatedParty = {
            id: user.relatedPartyId,
            name: user.username,
            role: user.relatedPartyRole
        };

        dispatch(setRelatedParty(relatedParty));
        dispatch(setEmail(user.email));
    }, [dispatch]);

    const clearAuthState = useCallback(() => {
        dispatch(setAuthStatus(false));
        dispatch(setRelatedParty(null));
        dispatch(setEmail(null));
    }, [dispatch]);

    const handleSessionLost = useCallback(() => {
        clearAuthState();
        if (typeof window !== "undefined") {
            window.dispatchEvent(new CustomEvent("auth:login-required"));
        }
    }, [clearAuthState]);

    const handleFullSessionExpired = useCallback(() => {
        clearAuthState();
        navigate('/home', {
            replace: true,
            state: {
                sessionExpired: true
            }
        });
    }, [clearAuthState, navigate]);

    const login = useCallback(async (email, password) => {
        try {
            await ensureCsrf();
            const res = await apiClient.post("/auth/login", {email, password});
            const data = res.data || {};

            if (data.success === false) {
                return {
                    success: false,
                    message: data.message || data.error || "Login failed"
                };
            }

            if (!data?.user) {
                return {success: false, message: "Login failed"};
            }

            const {user, serviceSession} = data;
            const {ok} = validateUserFields(user, serviceSession === true);
            if (!ok) {
                return {success: false, message: "Invalid user data received"};
            }

            sessionExpiredHandled.current = false;
            fullSessionExpiredHandled.current = false;
            updateAuthState(data);
            return {success: true};
        } catch (e) {
            console.error("Login error:", e);
            return parseApiError(e, "Login failed");
        }
    }, [updateAuthState]);

    const register = useCallback(async (registrationData) => {
        try {
            await ensureCsrf();
            const res = await apiClient.post("/auth/register", registrationData);
            const data = res.data || {};

            if (data.success === false) {
                return {
                    success: false,
                    message: data.message || data.error || "Registration failed",
                };
            }

            if (data.user) {
                const {user, serviceSession} = data;
                const {ok} = validateUserFields(user, serviceSession === true);
                if (!ok) {
                    return {success: false};
                }
                updateAuthState(data);
                return {success: true};
            }

            return {success: true};
        } catch (e) {
            console.error("Registration error:", e);
            return parseApiError(e, "Registration failed");
        }
    }, [updateAuthState]);

    const logout = useCallback(async () => {
        try {
            await ensureCsrf();
            await apiClient.post("/auth/logout");
        } catch (error) {
            console.error("Logout error:", error);
        }

        sessionExpiredHandled.current = false;
        fullSessionExpiredHandled.current = false;
        clearAuthState();
        return {success: true};
    }, [clearAuthState]);

    useEffect(() => {
        (async () => {
            await ensureCsrf();
            await loadMe(updateAuthState, clearAuthState);
        })();
    }, [updateAuthState, clearAuthState]);

    useEffect(() => {
        if (!isAuthenticated) {
            if (pollTimerRef.current) {
                clearInterval(pollTimerRef.current);
                pollTimerRef.current = null;
            }
            return;
        }

        pollTimerRef.current = setInterval(() => {
            loadMe(updateAuthState, clearAuthState);
        }, SESSION_POLL_INTERVAL);

        return () => {
            if (pollTimerRef.current) {
                clearInterval(pollTimerRef.current);
                pollTimerRef.current = null;
            }
        };
    }, [isAuthenticated, updateAuthState, clearAuthState]);

    useEffect(() => {
        const handleServiceSessionActive = () => {
            clearAuthState();
        };

        window.addEventListener('auth:service-session-active', handleServiceSessionActive);
        return () => window.removeEventListener('auth:service-session-active', handleServiceSessionActive);
    }, [clearAuthState]);

    useEffect(() => {
        const handleSessionExpired = () => {
            if (sessionExpiredHandled.current) {
                return;
            }

            sessionExpiredHandled.current = true;
            handleSessionLost();
        };

        window.addEventListener('auth:session-expired', handleSessionExpired);
        return () => window.removeEventListener('auth:session-expired', handleSessionExpired);
    }, [handleSessionLost]);

    useEffect(() => {
        const handleFullExpired = () => {
            if (fullSessionExpiredHandled.current) {
                return;
            }

            fullSessionExpiredHandled.current = true;
            handleFullSessionExpired();
        };

        window.addEventListener('auth:full-session-expired', handleFullExpired);
        return () => window.removeEventListener('auth:full-session-expired', handleFullExpired);
    }, [handleFullSessionExpired]);

    return (
        <AuthContext.Provider value={{login, register, logout}}>
            {children}
        </AuthContext.Provider>
    );
};