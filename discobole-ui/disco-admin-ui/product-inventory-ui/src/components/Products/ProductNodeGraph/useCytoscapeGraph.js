// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect, useMemo, useState} from "react";
import cytoscape from "cytoscape";
import dagre from "cytoscape-dagre";
import nodeHtmlLabel from "cytoscape-node-html-label";
import contextMenus from "cytoscape-context-menus";
import {MIGRATION_RELATIONSHIP, PRODUCT_OPERATIONAL_STATUSES} from "../../../common/constants.js";

export const useCytoscapeGraph = (
    product,
    productHierarchy,
    includeReliesFromEdges,
    setPopoverData,
    setPopoverPosition
) => {
    const [loadingDetails, setLoadingDetails] = useState(true);
    const [graphError, setGraphError] = useState(false);

    const currentId = product?.id;

    // Initialize cytoscape plugins once
    useMemo(() => {
        cytoscape.use(dagre);
        if (typeof cytoscape("core", "contextMenus") === "undefined") {
            contextMenus(cytoscape);
        }
        if (typeof cytoscape("core", "nodeHtmlLabel") === "undefined") {
            nodeHtmlLabel(cytoscape);
        }
    }, []);

    const {data, migrationEdgesData} = useMemo(() => {
        const data = [];
        const migrationEdgesData = [];
        const addedNodes = new Set();
        const addedEdges = new Set();

        const getProductKindAndType = (productNode) => {
            let kind = "SpecificationNodes";
            let typeProduct = "ProductSpecificationRef";

            if (productNode?.productOffering) {
                const offeringType = productNode.productOffering["@type"];
                const typeMap = {
                    "BundleProductOffering": {kind: "bundleNodes", type: "BundleProductOffering"},
                    "AtomicProductOffering": {kind: "AtomicNodes", type: "AtomicProductOffering"},
                    "Contract": {kind: "product", type: "Contract"}
                };

                if (typeMap[offeringType]) {
                    kind = typeMap[offeringType].kind;
                    typeProduct = typeMap[offeringType].type;
                }
            }

            const nodeType = productNode["@type"];
            if (nodeType === "PhysicalProduct") {
                kind = `physical${kind}`;
                typeProduct = `PhysicalProduct\n${typeProduct}`;
            } else if (nodeType === "ShipmentProduct") {
                kind = `shipment${kind}`;
                typeProduct = `ShipmentProduct\n${typeProduct}`;
            }

            return {kind, typeProduct};
        };

        const createNodeData = (productNode) => {
            const {kind, typeProduct} = getProductKindAndType(productNode);

            return {
                classes: "nodeIcon",
                group: "nodes",
                data: {
                    kind: kind || "product",
                    type: productNode["@type"],
                    TypeProduct: typeProduct,
                    displayName: productNode.name || " ",
                    id: productNode.id,
                    startDate: productNode.startDate,
                    lastUpdateDate: productNode.lastUpdateDate,
                    terminationDate: productNode.terminationDate,
                    isMigrationCase: productNode.isMigrationCase,
                    creationDate: productNode.creationDate,
                    status: productNode.status,
                    operationalStatus: productNode.operationalStatus,
                    productId: "-",
                    relatedProductOrderItem: "",
                    errorCode: productNode.errorMessage,
                },
            };
        };

        const addNode = (productNode) => {
            if (productNode?.id && !addedNodes.has(productNode.id)) {
                data.push(createNodeData(productNode));
                addedNodes.add(productNode.id);
            }
        };

        const addEdge = (sourceId, targetId, relationshipType) => {
            if (!sourceId || !targetId || !addedNodes.has(sourceId) || !addedNodes.has(targetId)) {
                return;
            }

            const edgeKey = `${sourceId}-${targetId}`;
            if (addedEdges.has(edgeKey)) return;

            const edgeData = {
                data: {
                    group: "edges",
                    source: sourceId,
                    target: targetId,
                    label: relationshipType,
                }
            };

            if (MIGRATION_RELATIONSHIP.includes(relationshipType)) {
                migrationEdgesData.push({...edgeData, classes: 'dashed-edge'});
            } else if (includeReliesFromEdges || relationshipType !== "reliesFrom") {
                data.push(edgeData);
            }

            addedEdges.add(edgeKey);
        };

        const processProductHierarchy = () => {
            if (!productHierarchy || !Array.isArray(productHierarchy)) {
                return;
            }

            try {
                // First pass: Add all nodes
                productHierarchy.forEach((productNode) => {
                    addNode(productNode);

                    // Add related nodes if they exist in hierarchy
                    productNode?.productRelationship?.forEach((relationship) => {
                        const relatedProduct = relationship.product;
                        if (relatedProduct?.id) {
                            const fullRelatedProduct = productHierarchy.find(p => p.id === relatedProduct.id);
                            if (fullRelatedProduct) {
                                addNode(fullRelatedProduct);
                            }
                        }
                    });
                });

                // Second pass: Add edges
                productHierarchy.forEach((productNode) => {
                    productNode?.productRelationship?.forEach((relationship) => {
                        if (relationship.relationshipType !== "rootProduct") {
                            addEdge(productNode.id, relationship.product?.id, relationship.relationshipType);
                        }
                    });
                });
            } catch (error) {
                console.error("Error processing product hierarchy:", error);
                throw error;
            }
        };

        processProductHierarchy();
        return {data, migrationEdgesData};
    }, [productHierarchy, product, includeReliesFromEdges]);

    const createNodeTemplate = (data, isHover = false, isSelected = false) => {
        const isCurrentNode = data.id === currentId;
        const hasPendingMigrate = data.operationalStatus === PRODUCT_OPERATIONAL_STATUSES.PendingMigrate;

        const graphicClass = isSelected || isCurrentNode
            ? `selected ${data.status}`
            : isHover
                ? `hover icon-${data.kind}-${data.status}`
                : `icon-${data.kind}-${data.status}`;

        const iconClass = isHover ? "icon-hover" : "";

        return `
            <div class="element ${data._hidden}">
                <span class="element-severity_badge">
                    <i class="icon icon-${data.status}"></i>
                </span>
                ${hasPendingMigrate ? `
                    <span class="element-severity_badge-right">
                        ${data.operationalStatus}
                    </span>
                ` : ''}
                <span class="element-graphic square ${graphicClass}">
                    <i class="icon icon-${data.kind} ${iconClass}"></i>
                    <span class="overlay"></span>
                </span>
                <span title="${data.displayName}" class="element-label">${data.displayName}</span>
            </div>
        `;
    };

    useEffect(() => {
        const container = document.getElementById("cy");
        if (!container) return;

        try {
            setLoadingDetails(true);
            setGraphError(false);

            const cy = cytoscape({
                container,
                style: [
                    {
                        selector: "core",
                        css: {"active-bg-size": 0}
                    },
                    {
                        selector: "node",
                        css: {
                            shape: "rectangle",
                            width: "38px",
                            height: "38px",
                            "font-family": "Nokia Pure Regular",
                            "background-opacity": 1,
                            color: "black",
                        }
                    },
                    {
                        selector: "edge",
                        css: {
                            "curve-style": "bezier",
                            width: 1.5,
                            "line-color": "#b8b8b8",
                            "target-arrow-color": "#ccc",
                            "target-arrow-shape": "triangle",
                            label: "data(label)",
                            "font-size": "8px",
                        }
                    }
                ],
                layout: {
                    name: "breadthfirst",
                    directed: true,
                    padding: 10,
                    avoidOverlap: true,
                    spacingFactor: 1.2,
                    nodeSep: 80,
                    rankSep: 100,
                    nodeDimensionsIncludeLabels: true,
                    rankDir: "TB",
                    ranker: "longest-path",
                    animate: true,
                    animationDuration: 500,
                    transform: (node, position) =>
                        node.data("id") === currentId ? {x: position.x, y: position.y} : position
                },
                elements: data,
                zoomingEnabled: true,
                userZoomingEnabled: true,
                autoungrabify: false,
                wheelSensitivity: 0.2,
                minZoom: 0.5,
                maxZoom: 5,
            });

            cy.reset();
            cy.zoom({level: 1.6});

            // Event handlers
            cy.on("mouseover", "node", (e) => e.target.addClass("hover"));
            cy.on("mouseout", "node", (e) => e.target.removeClass("hover"));
            cy.on("mousedown", "node", (e) => e.target.addClass("hover"));

            cy.on("click", "node", function (e) {
                const clickedNodeData = this.data();
                const isRelevantNode = clickedNodeData.id === product.id ||
                    productHierarchy.some(item => item.id === clickedNodeData.id);

                if (isRelevantNode) {
                    setPopoverData(clickedNodeData);
                    setPopoverPosition({x: e.renderedPosition.x, y: e.renderedPosition.y});
                }
            });

            // Center graph
            const boundingBox = cy.nodes().boundingBox();
            cy.panBy({
                x: -boundingBox.x1 - 100,
                y: -boundingBox.y1 - 300,
            });

            // HTML Labels
            cy.nodeHtmlLabel([
                {
                    query: ".nodeIcon",
                    halign: "center",
                    valign: "center",
                    halignBox: "center",
                    valignBox: "center",
                    tpl: (data) => createNodeTemplate(data)
                },
                {
                    query: ".nodeIcon.hover",
                    halign: "center",
                    valign: "center",
                    halignBox: "center",
                    valignBox: "center",
                    tpl: (data) => createNodeTemplate(data, true)
                },
                {
                    query: ".nodeIcon:selected",
                    halign: "center",
                    valign: "center",
                    halignBox: "center",
                    valignBox: "center",
                    tpl: (data) => createNodeTemplate(data, false, true)
                }
            ]);

            // Add migration edges
            if (migrationEdgesData?.length > 0) {
                const positions = {};
                cy.nodes().forEach((node) => {
                    positions[node.id()] = node.position();
                });

                const validMigrationEdges = migrationEdgesData.filter((edge) =>
                    cy.getElementById(edge.data.source).length > 0 &&
                    cy.getElementById(edge.data.target).length > 0
                );

                if (validMigrationEdges.length > 0) {
                    cy.add(validMigrationEdges);

                    cy.nodes().forEach((node) => {
                        if (positions[node.id()]) {
                            node.position(positions[node.id()]);
                        }
                    });

                    cy.style()
                        .selector('edge.dashed-edge')
                        .style({
                            'line-style': 'dashed',
                            'line-dash-pattern': [10, 8],
                            'width': 2,
                            'line-color': '#b8b8b8'
                        })
                        .update();
                }
            }

            setLoadingDetails(false);
        } catch (error) {
            console.error("Error in graph:", error);
            setGraphError(true);
            setLoadingDetails(false);
        }
    }, [productHierarchy, includeReliesFromEdges, data, migrationEdgesData, currentId, product, setPopoverData, setPopoverPosition]);

    return {loadingDetails, graphError};
};