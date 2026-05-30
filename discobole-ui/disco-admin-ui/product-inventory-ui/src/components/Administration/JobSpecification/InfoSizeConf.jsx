// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import api from "../../../service/ProductInventoryAPI.js";

const BYTES_PER_GB = 1024 ** 3;

const InfoSizeConf = () => {
    const [exportLimit, setExportLimit] = useState(null);

    useEffect(() => {
        api.getConfiguration()
            .then((config) => setExportLimit(config?.exportLimit ?? null))
            .catch((err) => console.error("Error fetching configuration:", err));
    }, []);

    if (exportLimit === null) return null;

    return (
        <div className="alert alert-info" role="alert">
            <span className="alert-icon">
                <span className="visually-hidden">Info</span>
            </span>
            <p>Maximum export size: {(exportLimit / BYTES_PER_GB).toFixed(2)} GB</p>
        </div>
    );
};

export default InfoSizeConf;