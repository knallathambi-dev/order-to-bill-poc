// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const LoadingIndicator = () => (
    <div className="d-flex justify-content-center align-items-center py-5">
        <div className="spinner-border spinner-border-lg text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
        </div>
    </div>
);

export default LoadingIndicator;