// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

const Breadcrumb = () => {
    return (
        <nav aria-label="breadcrumb">
            <ol className="breadcrumb mb-0">
                <li className="breadcrumb-item"><a href="/">Orders</a></li>
                <li className="breadcrumb-item active" aria-current="page">Order Details</li>
            </ol>
        </nav>
    );
};

export default Breadcrumb;