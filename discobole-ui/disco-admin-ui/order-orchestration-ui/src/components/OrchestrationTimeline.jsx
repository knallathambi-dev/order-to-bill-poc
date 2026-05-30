// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useMemo} from 'react';
import PropTypes from 'prop-types';
import TimelineChart from './charts/TimelineChart';
import {STATUS_COMPLETED, STATUS_EXECUTED} from '../utils/constants';

const SEPARATOR_DURATION_MS = 1000;
const DEFAULT_NODE_LABEL = 'Order Item';
const COLOR_PALETTE = {
    'Tangible Product + Shipment': '#50BE87',
    'Fixed Delivery Factory': '#A885D8',
    'Mobile Delivery Factory': '#4BB4E6',
    'Partner Factory': '#FFB4E6',
    'Overall Plan': '#FF7900',
    'Separator': 'transparent',
};

/**
 * Transforms orchestration plan data into timeline items for visualization.
 */
export function mapPlanToTimelineItems(plan) {
    if (!plan?.orchestrationPlanNodes?.length) return [];

    const nodes = plan.orchestrationPlanNodes;
    const sortedNodes = sortNodesByDeliverAfter(nodes);
    const rootNodes = findRootNodes(sortedNodes);
    const items = [];

    sortedNodes.forEach((node) => {
        if (
            !node?.orchestrationNodeSchedule?.actualOrderItemStartDate ||
            !node?.orchestrationNodeSchedule?.actualOrderItemCompletionDate
        ) {
            return;
        }

        const startMs = new Date(node.orchestrationNodeSchedule.actualOrderItemStartDate).getTime();
        const endMs = new Date(node.orchestrationNodeSchedule.actualOrderItemCompletionDate).getTime();

        if (!Number.isFinite(startMs) || !Number.isFinite(endMs) || endMs < startMs) {
            return;
        }

        const timelineItem = createTimelineItem(node, startMs, endMs, rootNodes);
        if (timelineItem.type === null) return;

        items.push(timelineItem);

        if (shouldAddSeparator(node.id, rootNodes, sortedNodes)) {
            items.push(createSeparatorItem(node.id, endMs));
        }
    });

    if (items.length > 0) {
        const overallPlanItem = createOverallPlanItem(plan);
        if (overallPlanItem) {
            items.push(overallPlanItem);
        }
    }

    return items;
}

/**
 * Main timeline component that renders orchestration plan execution timeline.
 */
const OrchestrationTimeline = ({plan}) => {
    const items = useMemo(() => mapPlanToTimelineItems(plan), [plan]);
    const isExecuted = plan?.state === STATUS_EXECUTED;
    const allNodesCompleted = plan?.orchestrationPlanNodes?.every(
        (node) => node.state === STATUS_COMPLETED,
    );

    if (!isExecuted) {
        return (
            <div className="p-5 text-center">
                Schedule will be available once the plan is executed.
            </div>
        );
    }

    if (!allNodesCompleted) {
        return (
            <div className="p-5 text-center">
                Schedule is not available because one or more nodes are not completed.
            </div>
        );
    }

    return <TimelineChart items={items} height={500} colorByType={COLOR_PALETTE}/>;
};

export default OrchestrationTimeline;

OrchestrationTimeline.propTypes = {
    plan: PropTypes.shape({
        state: PropTypes.string,
        orchestrationPlanNodes: PropTypes.arrayOf(PropTypes.object),
        orderStartDate: PropTypes.string,
        actualOrderStartDate: PropTypes.string,
        actualOrderCompletionDate: PropTypes.string,
        actualOrderDeliveryLeadTime: PropTypes.number,
    }),
};

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

function createTimelineItem(node, startMs, endMs, rootNodes) {
    return {
        id: node.id,
        label: resolveNodeLabel(node),
        type: resolveNodeType(node),
        startDate: node.orchestrationNodeSchedule.orderItemStartDate,
        actualStartDate: new Date(startMs).toISOString(),
        actualCompletionDate: new Date(endMs).toISOString(),
        actualLeadTimeSeconds: node.orchestrationNodeSchedule.actualOrderItemDeliveryLeadTime,
        isRootNode: rootNodes.has(node.id),
    };
}

function createSeparatorItem(nodeId, endMs) {
    return {
        id: `separator-${nodeId}`,
        label: '',
        actualStartDate: new Date(endMs).toISOString(),
        actualCompletionDate: new Date(endMs + SEPARATOR_DURATION_MS).toISOString(),
        isSeparator: true,
        type: 'Separator',
    };
}

function createOverallPlanItem(plan) {
    const schedule = plan?.orchestrationPlanSchedule;
    if (!schedule?.orderStartDate || !schedule?.actualOrderCompletionDate) {
        return null;
    }

    const startDate = new Date(schedule.orderStartDate);
    const completionDate = new Date(schedule.actualOrderCompletionDate);

    if (isNaN(startDate.getTime()) || isNaN(completionDate.getTime())) {
        return null;
    }

    return {
        id: 'overall-plan',
        type: 'Overall Plan',
        label: 'Overall Plan',
        startDate: startDate.toISOString(),
        actualStartDate: new Date(schedule.actualOrderStartDate).toISOString(),
        actualCompletionDate: completionDate.toISOString(),
        actualLeadTimeSeconds: schedule.actualOrderDeliveryLeadTime,
    };
}

function shouldAddSeparator(nodeId, rootNodes, sortedNodes) {
    if (!rootNodes.has(nodeId)) return false;
    return isIndependentWorkflowBranch(nodeId, sortedNodes);
}

function isIndependentWorkflowBranch(nodeId, nodes) {
    return !nodes.some(
        (node) =>
            hasDeliverAfterDependency(node, nodeId) ||
            hasProductDependency(node, nodeId, nodes),
    );
}

function hasDeliverAfterDependency(node, targetNodeId) {
    return node.relatedOrchestrationPlanNode?.some(
        (rel) => rel?.relationshipType === 'DeliverAfter' && rel?.relatedNodeId === targetNodeId,
    );
}

function hasProductDependency(node, targetNodeId, allNodes) {
    return node.relatedProduct?.some((product) => {
        if (product?.relationshipType !== 'reliesOn' || !product?.id) return false;

        return allNodes.some((deliverNode) => {
            if (deliverNode.id !== targetNodeId) return false;
            return deliverNode.relatedProduct?.some(
                (dp) => dp?.relationshipType === 'delivers' && dp?.id === product.id,
            );
        });
    });
}

function resolveNodeLabel(node) {
    const product = node?.relatedProduct?.find(
        (p) =>
            p?.relationshipType === 'delivers' &&
            String(p['@type'] || '').toLowerCase() !== 'shipmentproduct',
    );
    return product?.productSpecification?.name || DEFAULT_NODE_LABEL;
}

function resolveNodeType(node) {
    const serviceOrderRef = node?.relatedServiceOrder?.serviceOrderManagementRef;

    if (!serviceOrderRef) {
        const delivers = node?.relatedProduct?.find((p) => p?.relationshipType === 'delivers');
        const productType = String(delivers?.['@type'] || '').toLowerCase();
        if (productType.includes('physical')) return 'Tangible Product + Shipment';
        return null;
    }

    const url = String(serviceOrderRef).toLowerCase();
    if (url.includes('partner-factory')) return 'Partner Factory';
    if (url.includes('mobile-factory')) return 'Mobile Delivery Factory';
    if (url.includes('fix-factory')) return 'Fixed Delivery Factory';
    return null;
}

function sortNodesByDeliverAfter(nodes) {
    const result = [...nodes];

    for (const node of result) {
        const deliverAfterRels =
            node.relatedOrchestrationPlanNode?.filter(
                (rel) => rel?.relationshipType === 'DeliverAfter' && rel?.relatedNodeId,
            ) || [];

        for (const rel of deliverAfterRels) {
            const refNodeIndex = result.findIndex((n) => n.id === rel.relatedNodeId);
            const currentIndex = result.findIndex((n) => n.id === node.id);

            if (refNodeIndex !== -1 && currentIndex !== -1 && currentIndex !== refNodeIndex + 1) {
                const [movedNode] = result.splice(currentIndex, 1);
                const newRefIndex = result.findIndex((n) => n.id === rel.relatedNodeId);
                result.splice(newRefIndex + 1, 0, movedNode);
            }
        }
    }
    return result;
}

function findRootNodes(nodes) {
    const rootNodes = new Set();
    for (const node of nodes) {
        const hasDeliverAfterDep = node.relatedOrchestrationPlanNode?.some(
            (rel) => rel?.relationshipType === 'DeliverAfter' && rel?.relatedNodeId,
        );
        const hasReliesOnDep = node.relatedProduct?.some(
            (product) => product?.relationshipType === 'reliesOn' && product?.id,
        );
        if (!hasDeliverAfterDep && !hasReliesOnDep) {
            rootNodes.add(node.id);
        }
    }
    return rootNodes;
}