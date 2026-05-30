// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SET_SELECTED_OFFER_NAME = "SET_SELECTED_OFFER_NAME";
export const SET_SELECTED_OFFER_ID = "SET_SELECTED_OFFER_ID";
export const SET_DESCRIPTION = "SET_DESCRIPTION";

export const setSelectedOfferName = (name) => ({
    type: SET_SELECTED_OFFER_NAME,
    payload: name,
});

export const setSelectedOfferId = (id) => ({
    type: SET_SELECTED_OFFER_ID,
    payload: id,
});

export const setDescription = (description) => ({
    type: SET_DESCRIPTION,
    payload: description,
});