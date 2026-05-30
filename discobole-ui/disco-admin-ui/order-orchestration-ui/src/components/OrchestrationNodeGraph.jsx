// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import cytoscape from "cytoscape";
import dagre from "cytoscape-dagre";
import nodeHtmlLabel from "cytoscape-node-html-label";
import contextMenus from "cytoscape-context-menus";
import PropTypes from "prop-types";

import { NodePopoverBase, PopoverRow,StatusLegend } from "@discobole/common-ui";
import {
    getActionIconClassName,
    getNodeActionType,
    getRelatedProductOrderItem,
} from "../service/orchestrationUtils.js";
import { ACTION_TYPES } from "../utils/constants.js";

import "cytoscape-context-menus/cytoscape-context-menus.css";
import {TYPES} from "./OrchestrationPlanDetails.jsx";

// ── Register cytoscape extensions once ──────────────────────────────────────

cytoscape.use(dagre);

if (typeof cytoscape("core", "contextMenus") === "undefined") {
    contextMenus(cytoscape);
}
if (typeof cytoscape("core", "nodeHtmlLabel") === "undefined") {
    nodeHtmlLabel(cytoscape);
}

const CYTOSCAPE_STYLESHEET = [
    {
        selector: "core",
        style: { "active-bg-size": 0 },
    },
    {
        selector: "node",
        style: {
            width: 38,
            height: 38,
            "background-opacity": 0,
        },
    },
    {
        selector: "edge",
        style: {
            width: 1,
            "line-color": "#b8b8b8",
            "curve-style": "bezier",
            "target-arrow-color": "#ccc",
            "target-arrow-shape": "triangle",
        },
    },
];

const DAGRE_LAYOUT = {
    name: "dagre",
    rankDir: "TB",
    nodeSep: 60,
    rankSep: 80,
    padding: 30,
    fit: true,
    animate: false,
};

const NODE_HTML_LABEL_BASE = {
    halign: "center",
    valign: "center",
    halignBox: "center",
    valignBox: "center",
};

const buildNodeHtml = (data, variant = "default") => {
    const graphicClass =
        variant === "hover"
            ? "element-graphic hover"
            : variant === "selected"
                ? `element-graphic selected ${data.status}`
                : "element-graphic";

    const iconExtraClass = variant === "hover" ? "icon-hover" : "";

    return `
        <div class="element ${data._hidden || ""}">
            <span class="element-severity_badge">
                <i class="icon icon-${data.status}"></i>
            </span>
            <span class="${graphicClass} icon-${data.kind}-${data.status}">
                <i class="icon icon-${data.kind} ${iconExtraClass}"></i>
                <span class="overlay"></span>
            </span>
            <span title="${data.displayName}" class="element-label">
                ${data.displayName}
            </span>
        </div>`;
};

const getDeliversProduct = (relatedProducts) =>
    relatedProducts?.find(
        (p) =>
            p.relationshipType === "delivers" &&
            p["@type"]?.toLowerCase() !== "shipmentproduct"
    );

const getLabel = (node) =>
    getDeliversProduct(node?.relatedProduct)?.productSpecification?.name ?? null;

const getKind = (node) => {
    const product = node?.relatedProduct?.find(
        (p) => p.relationshipType === "delivers"
    );
    if (!product?.["@type"]) return null;

    const type = product["@type"].toLowerCase();
    if (type === "cfs") return "service";
    if (type === "shipmentproduct" || type === "physicalproduct") return "device";
    return null;
};

const getProductId = (node) => {
    const product = getDeliversProduct(node?.relatedProduct);
    return product?.id ?? "-";
};

const buildElements = (plan) => {
    if (!plan?.orchestrationPlanNodes) return [];

    const nodes = [
        {
            classes: "nodeIcon",
            group: "nodes",
            data: {
                id: plan.id,
                kind: "plan",
                displayName: "Delivery Plan",
                status: plan.state,
            },
        },
    ];

    const edges = [];

    for (const orchestrationNode of plan.orchestrationPlanNodes) {
        nodes.push({
            classes: "nodeIcon",
            group: "nodes",
            data: {
                id: orchestrationNode.id,
                kind: getKind(orchestrationNode),
                displayName: getLabel(orchestrationNode) || "Order Item",
                status: orchestrationNode.state,
                action: getNodeActionType(orchestrationNode),
                productId: getProductId(orchestrationNode),
                relatedProductOrderItem: getRelatedProductOrderItem(
                    orchestrationNode,
                    "delivers"
                ),
                errorCode: orchestrationNode.errorMessage,
            },
        });

        const relatedNodes = orchestrationNode.relatedOrchestrationPlanNode;

        if (relatedNodes?.length) {
            for (const relatedNode of relatedNodes) {
                edges.push({
                    data: {
                        source: relatedNode.relatedNodeId,
                        target: orchestrationNode.id,
                    },
                });
            }
        } else {
            edges.push({
                data: {
                    source: plan.id,
                    target: orchestrationNode.id,
                },
            });
        }
    }

    return [...nodes, ...edges];
};

const OrchestrationPopover = ({ node, position, onClose, onViewDetails }) => {
    const handleViewDetails = () => {
        onViewDetails();
        onClose();
    };

    return (
        <NodePopoverBase position={position} onClose={onClose}>
            <h4 className="node-popover__title">{node?.displayName}</h4>

            <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                <tbody className="fw-semibold">
                <PopoverRow label="Status">
                        <span className={`tag tag-sm status-value ${node?.status?.toLowerCase()}`}>
                            {node?.status}
                        </span>
                </PopoverRow>

                {node?.action && (
                    <PopoverRow label="Action">
                            <span className={`tag tag-sm action-type gap-2 ${node.action}`}>
                                <em className={`icon-${getActionIconClassName(node.action)} action-icon`} />
                                {ACTION_TYPES[node.action] ?? "No Change"}
                            </span>
                    </PopoverRow>
                )}

                <PopoverRow label="Node Id">{node.id}</PopoverRow>
                <PopoverRow label="Product Id">{node.productId}</PopoverRow>

                {node?.relatedProductOrderItem && (
                    <PopoverRow label="Product Order Item Id">
                        {node.relatedProductOrderItem}
                    </PopoverRow>
                )}

                {node?.errorCode && (
                    <PopoverRow label="Error Code" isError>
                        {node.errorCode[0]?.code}
                    </PopoverRow>
                )}
                </tbody>
            </table>

            <button className="btn btn-link more-btn" type="button" onClick={handleViewDetails}>
                View All Details
            </button>
        </NodePopoverBase>
    );
};

OrchestrationPopover.propTypes = {
    node: PropTypes.object.isRequired,
    position: PropTypes.shape({ x: PropTypes.number, y: PropTypes.number }).isRequired,
    onClose: PropTypes.func.isRequired,
    onViewDetails: PropTypes.func.isRequired,
};

function OrchestrationNodeGraph({ plan, setActiveTab, getIndexByIdViewDetails }) {
    const containerRef = useRef(null);
    const cyRef = useRef(null);
    const [popoverData, setPopoverData] = useState(null);
    const [popoverPosition, setPopoverPosition] = useState({ x: 0, y: 0 });

    const elements = useMemo(() => buildElements(plan), [plan]);

    const handleNodeClick = useCallback(
        (e) => {
            const nodeData = e.target.data();
            if (nodeData.kind === "plan") return;

            getIndexByIdViewDetails(nodeData.id);
            setPopoverData(nodeData);
            setPopoverPosition({
                x: e.renderedPosition.x,
                y: e.renderedPosition.y,
            });
        },
        [getIndexByIdViewDetails]
    );

    const closePopover = useCallback(() => setPopoverData(null), []);

    const handleViewDetails = useCallback(
        () => setActiveTab("details"),
        [setActiveTab]
    );

    useEffect(() => {
        if (!containerRef.current || !elements.length) return;

        const cy = cytoscape({
            container: containerRef.current,
            elements,
            style: CYTOSCAPE_STYLESHEET,
            layout: DAGRE_LAYOUT,
            zoomingEnabled: true,
            userZoomingEnabled: true,
            autoungrabify: false,
            minZoom: 0.3,
            maxZoom: 3,
        });

        cyRef.current = cy;

        cy.ready(() => {
            cy.fit(cy.elements(), 40);
        });

        cy.on("mouseover", "node", (e) => e.target.addClass("hover"));
        cy.on("mouseout", "node", (e) => e.target.removeClass("hover"));
        cy.on("click", "node", handleNodeClick);

        cy.nodeHtmlLabel([
            { ...NODE_HTML_LABEL_BASE, query: ".nodeIcon", tpl: (data) => buildNodeHtml(data, "default") },
            { ...NODE_HTML_LABEL_BASE, query: ".nodeIcon.hover", tpl: (data) => buildNodeHtml(data, "hover") },
            { ...NODE_HTML_LABEL_BASE, query: ".nodeIcon:selected", tpl: (data) => buildNodeHtml(data, "selected") },
        ]);

        const resizeObserver = new ResizeObserver((entries) => {
            for (const entry of entries) {
                const { width, height } = entry.contentRect;
                if (width > 0 && height > 0) {
                    cy.resize();
                    cy.fit(cy.elements(), 40);
                }
            }
        });
        resizeObserver.observe(containerRef.current);

        return () => {
            resizeObserver.disconnect();
            cy.destroy();
            cyRef.current = null;
        };
    }, [elements, handleNodeClick]);

    return (
        <div className="graph-container">
            <StatusLegend types={TYPES}/>
            <div ref={containerRef} className="graph-canvas" />

            {popoverData && (
                <OrchestrationPopover
                    node={popoverData}
                    position={popoverPosition}
                    onClose={closePopover}
                    onViewDetails={handleViewDetails}
                />
            )}
        </div>
    );
}

OrchestrationNodeGraph.propTypes = {
    plan: PropTypes.object.isRequired,
    setActiveTab: PropTypes.func.isRequired,
    getIndexByIdViewDetails: PropTypes.func.isRequired,
};

export default OrchestrationNodeGraph;