// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, { useEffect, useRef } from "react";
import PropTypes from "prop-types";

const NodePopoverBase = ({ position, onClose, children }) => {
    const popoverRef = useRef(null);

    useEffect(() => {
        const handleOutsideClick = (e) => {
            if (popoverRef.current && !popoverRef.current.contains(e.target)) {
                onClose();
            }
        };
        document.addEventListener("mousedown", handleOutsideClick);
        return () => document.removeEventListener("mousedown", handleOutsideClick);
    }, [onClose]);

    return (
        <div
            ref={popoverRef}
            className="node-popover"
            style={{ top: position.y, left: position.x + 50 }}
        >
            {children}
        </div>
    );
};

NodePopoverBase.propTypes = {
    position: PropTypes.shape({
        x: PropTypes.number.isRequired,
        y: PropTypes.number.isRequired,
    }).isRequired,
    onClose: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
};

export default NodePopoverBase;