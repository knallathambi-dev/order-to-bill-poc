// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {cloneElement, useEffect, useRef} from "react";
import {Tooltip} from "boosted";

const BootstrapTooltip = ({title = "Close", placement = "top", disposeOnAlertClose = false, children}) => {
    const ref = useRef(null);

    useEffect(() => {
        let tooltip;
        if (ref.current) {
            tooltip = new Tooltip(ref.current);
        }

        const alertEl = disposeOnAlertClose ? ref.current?.closest(".alert") : null;
        const handleAlertClose = () => tooltip?.dispose();

        if (alertEl) {
            alertEl.addEventListener("close.bs.alert", handleAlertClose);
        }

        return () => {
            tooltip?.dispose();
            if (alertEl) {
                alertEl.removeEventListener("close.bs.alert", handleAlertClose);
            }
        };
    }, [disposeOnAlertClose]);

    return cloneElement(children, {
        ref,
        "data-bs-toggle": "tooltip",
        "data-bs-placement": placement,
        "data-bs-title": title,
    });
};

export default BootstrapTooltip;