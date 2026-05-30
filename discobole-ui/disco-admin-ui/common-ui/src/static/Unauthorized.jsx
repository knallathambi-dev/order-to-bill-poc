// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {useNavigate} from "react-router-dom";

const Unauthorized = () => {
    const navigate = useNavigate();

    return (
        <div className="d-flex align-items-center justify-content-center" style={{minHeight: "60vh"}}>
            <div className="text-center">
                <div className="mb-4">
                    <span
                        className="display-1 fw-bold"
                        style={{color: "#cd3c14", fontSize: "6rem", lineHeight: 1}}
                    >
                        401
                    </span>
                </div>
                <h2 className="fw-bold mb-3">Unauthorized</h2>
                <p className="text-muted mb-4" style={{maxWidth: "400px", margin: "0 auto"}}>
                    You don't have permission to access this page. Contact your administrator if
                    you believe this is an error.
                </p>
                <button
                    className="btn btn-outline-secondary"
                    onClick={() => navigate(-1)}
                >
                    <em className="icon-back me-2" aria-hidden="true"/>
                    Go Back
                </button>
            </div>
        </div>
    );
};

export default Unauthorized;