// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_AUTH_STATUS = 'SET_AUTH_STATUS';
export const RESET_AUTH_STATE = 'RESET_AUTH_STATE';
export const SET_RELATED_PARTY_ID = 'SET_RELATED_PARTY_ID';
export const SET_RELATED_PARTY = 'SET_RELATED_PARTY';
export const SET_LANGUAGE = 'SET_LANGUAGE';
export const SET_EMAIL = 'SET_EMAIL';

export function setAuthStatus(isAuthenticated) {
    return {
        type: SET_AUTH_STATUS,
        payload: {isAuthenticated},
    };
}

export function resetAuthState() {
    return {type: RESET_AUTH_STATE};
}

export function setRelatedParty(relatedParty) {
    return {
        type: SET_RELATED_PARTY,
        payload: {
            id: relatedParty?.id ?? "",
            name: relatedParty?.name ?? "",
            role: relatedParty?.role ?? "",
        },
    };
}

export function setLanguage(lang) {
    return {
        type: SET_LANGUAGE,
        payload: {lang},
    };
}

export function setEmail(email) {
    return {
        type: SET_EMAIL,
        payload: {email},
    };
}