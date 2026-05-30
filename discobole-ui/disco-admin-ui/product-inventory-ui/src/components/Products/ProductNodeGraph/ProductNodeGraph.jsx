// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import PropTypes from "prop-types";
import {LoadingIndicator, StatusLegend, StatusPanel} from "@discobole/common-ui";
import NodePopover from "./NodePopover";
import {useCytoscapeGraph} from "./useCytoscapeGraph";

import "cytoscape-context-menus/cytoscape-context-menus.css";
import {TYPES} from "../ProductsDetails.jsx";

function ProductNodeGraph({product, productHierarchy, setActiveTab}) {
    const [includeReliesFromEdges, setIncludeReliesFromEdges] = useState(false);
    const [popoverData, setPopoverData] = useState(null);
    const [popoverPosition, setPopoverPosition] = useState({x: 0, y: 0});

    const {loadingDetails, graphError} = useCytoscapeGraph(
        product,
        productHierarchy,
        includeReliesFromEdges,
        setPopoverData,
        setPopoverPosition
    );

    if (!product?.id) return null;

    if (graphError) {
        return (
            <StatusPanel
                variant="error"
                title="Graph unavailable"
                message="We encountered an issue while loading the graph. Please try again later."
                onAction={() => window.location.reload()}
                actionLabel="Reload"
            />
        );
    }

    return (
        <>
            <div className="form-check form-switch">
                <input
                    className="form-check-input"
                    type="checkbox"
                    id="reliesFromToggle"
                    checked={includeReliesFromEdges}
                    onChange={() => setIncludeReliesFromEdges((prev) => !prev)}
                />
                <label className="form-check-label" htmlFor="reliesFromToggle">
                    Display reliesFrom relationships
                </label>
            </div>

            <div className="graph-container">
                <StatusLegend types={TYPES}/>
                <div id="cy" className="graph-canvas"/>
            </div>

            {loadingDetails ? (
                <LoadingIndicator/>
            ) : (
                popoverData && (
                    <NodePopover
                        node={popoverData}
                        onClose={() => setPopoverData(null)}
                        position={popoverPosition}
                        setActiveTab={setActiveTab}
                    />
                )
            )}
        </>
    );
}

ProductNodeGraph.propTypes = {
    product: PropTypes.shape({
        id: PropTypes.string.isRequired,
    }).isRequired,
    productHierarchy: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.string.isRequired,
            name: PropTypes.string,
            status: PropTypes.string,
            operationalStatus: PropTypes.string,
            startDate: PropTypes.string,
            lastUpdateDate: PropTypes.string,
            terminationDate: PropTypes.string,
            creationDate: PropTypes.string,
            errorMessage: PropTypes.arrayOf(
                PropTypes.shape({
                    code: PropTypes.string,
                })
            ),
            productRelationship: PropTypes.arrayOf(
                PropTypes.shape({
                    product: PropTypes.shape({
                        id: PropTypes.string.isRequired,
                    }),
                    relationshipType: PropTypes.string,
                })
            ),
            productOffering: PropTypes.shape({
                "@type": PropTypes.string,
            }),
            "@type": PropTypes.string,
            isRoot: PropTypes.bool,
        })
    ).isRequired,
    setActiveTab: PropTypes.func.isRequired,
};

export default ProductNodeGraph;