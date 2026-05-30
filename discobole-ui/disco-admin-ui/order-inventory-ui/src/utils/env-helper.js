// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const env = window.__ENV__ ?? {
    GATEWAY_URL: import.meta.env.VITE_GATEWAY_URL,
    STANDALONE_MODE: import.meta.env.VITE_STANDALONE_MODE
};