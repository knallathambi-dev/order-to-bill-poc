// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";
import { formatToLocalDateTime, NodePopoverBase, PopoverRow } from "@discobole/common-ui";

const formatDate = (date) => (date ? formatToLocalDateTime(date) : "_");

const NodePopover = ({ node, onClose, position, setActiveTab }) => {
    const navigate = useNavigate();

    const handleViewDetails = useCallback(() => {
        try {
            navigate(`/product-inventory/products-details-page/${node?.id}?tab=Details`);
            setActiveTab("Details");
        } catch (error) {
            console.error(error);
        }
    }, [navigate, node?.id, setActiveTab]);

    return (
        <NodePopoverBase position={position} onClose={onClose}>
            <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                <tbody className="fw-semibold">
                <PopoverRow label="Id">{node?.id}</PopoverRow>
                <PopoverRow label="Product Name">{node?.displayName}</PopoverRow>

                <tr>
                    <td className="text-muted text-start">
                        <div className="d-flex align-items-center">Type</div>
                    </td>
                    <td className="fw-bold text-end" style={{ whiteSpace: "pre-line" }}>
                        {node?.TypeProduct}
                    </td>
                </tr>

                <PopoverRow label="Start Date">
                    {formatDate(node?.startDate)}
                </PopoverRow>
                <PopoverRow label="Last Update Date">
                    {formatDate(node?.lastUpdateDate)}
                </PopoverRow>
                <PopoverRow label="Termination Date">
                    {formatDate(node?.terminationDate)}
                </PopoverRow>
                <PopoverRow label="Creation Date">
                    {formatDate(node?.creationDate)}
                </PopoverRow>

                {node?.errorCode?.length > 0 && (
                    <PopoverRow label="Error Code" isError>
                        {node.errorCode[0]?.code}
                    </PopoverRow>
                )}
                </tbody>
            </table>

            <button
                className="btn btn-link more-btn"
                type="button"
                onClick={handleViewDetails}
            >
                View All Details
            </button>
        </NodePopoverBase>
    );
};

NodePopover.propTypes = {
    node: PropTypes.shape({
        id: PropTypes.string.isRequired,
        displayName: PropTypes.string,
        TypeProduct: PropTypes.string,
        startDate: PropTypes.string,
        lastUpdateDate: PropTypes.string,
        terminationDate: PropTypes.string,
        creationDate: PropTypes.string,
        errorCode: PropTypes.arrayOf(
            PropTypes.shape({
                code: PropTypes.string,
            })
        ),
    }),
    onClose: PropTypes.func.isRequired,
    position: PropTypes.shape({
        x: PropTypes.number.isRequired,
        y: PropTypes.number.isRequired,
    }).isRequired,
    setActiveTab: PropTypes.func.isRequired,
};

export default NodePopover;