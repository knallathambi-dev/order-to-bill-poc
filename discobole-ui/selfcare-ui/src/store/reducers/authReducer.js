// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    RESET_AUTH_STATE,
    SET_AUTH_STATUS,
    SET_EMAIL,
    SET_LANGUAGE,
    SET_RELATED_PARTY,
    SET_RELATED_PARTY_ID
} from "../actions/authActions";

const INITIAL_STATE = {
    isAuthenticated: false,
    lang: 'en',
    email: '',
    relatedParty: {
        id: '',
        name: '',
        role: '',
    },
};

export default function authReducer(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_AUTH_STATUS: {
            const {isAuthenticated} = action.payload || {};
            const boolValue = !!isAuthenticated;
            return {
                ...state,
                isAuthenticated: boolValue,
            };
        }

        case SET_RELATED_PARTY: {
            const rp = action.payload || {};
            return {
                ...state,
                relatedParty: {
                    ...state.relatedParty,
                    ...rp,
                },
            };
        }

        case SET_RELATED_PARTY_ID: {
            const {id} = action.payload || {};
            return {
                ...state,
                relatedParty: {
                    ...state.relatedParty,
                    id: id ?? state.relatedParty.id,
                },
            };
        }

        case SET_LANGUAGE: {
            const {lang} = action.payload || {};
            return {
                ...state,
                lang: lang ?? state.lang,
            };
        }

        case SET_EMAIL: {
            const {email} = action.payload || {};
            return {
                ...state,
                email: email ?? state.email,
            };
        }

        case RESET_AUTH_STATE:
            return {...INITIAL_STATE};

        default:
            return state;
    }
}