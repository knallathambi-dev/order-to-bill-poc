// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {SET_DESCRIPTION, SET_SELECTED_OFFER_ID, SET_SELECTED_OFFER_NAME,} from "../actions/offerActions";

const INITIAL_STATE = {
    selectedOfferName: "",
    selectedOfferId: "",
    description: "",
};

export default function offerSlice(state = INITIAL_STATE, action) {
    switch (action.type) {
        case SET_SELECTED_OFFER_NAME:
            return {...state, selectedOfferName: action.payload};
        case SET_SELECTED_OFFER_ID:
            return {...state, selectedOfferId: action.payload};
        case SET_DESCRIPTION:
            return {...state, description: action.payload};
        default:
            return state;
    }
}