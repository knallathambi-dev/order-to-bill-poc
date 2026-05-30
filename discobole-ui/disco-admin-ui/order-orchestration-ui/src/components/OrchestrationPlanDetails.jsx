// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback, useEffect, useMemo, useState} from 'react';
import OrchestrationAccordionItem from './OrchestrationNodeAccordionItem';
import OrchestrationNodeGraph from './OrchestrationNodeGraph';
import OrchestrationTimeline from './OrchestrationTimeline';
import {BootstrapTooltip, ErrorMessageBox, useFederationConfig, useNavigation} from '@discobole/common-ui';
import PropTypes from 'prop-types';

import {ReactComponent as InitializedSVG} from '../assets/images/ic_Initialized.svg';
import {ReactComponent as AcknowledgedSVG} from '../assets/images/ic_Acknowledged.svg';
import {ReactComponent as InProgressSVG} from '../assets/images/ic_InProgress.svg';
import {ReactComponent as InDeliverySVG} from '../assets/images/ic_InDelivery.svg';
import {ReactComponent as HeldSVG} from '../assets/images/ic_Held.svg';
import {ReactComponent as AbortedSVG} from '../assets/images/aborted-sm.svg';
import {ReactComponent as RejectedSVG} from '../assets/images/ic_Rejected.svg';
import {ReactComponent as FailedSVG} from '../assets/images/ic_Failed.svg';
import {ReactComponent as CompletedSVG} from '../assets/images/ic_Completed.svg';

import service from '../service/OrchestrationDeliveryService.js';
import {
    getActionIconClassName,
    getLeadTime,
    getNodeActionType,
    isResolvedErrorMessage,
} from '../service/orchestrationUtils.js';
import {
    ACTION_TYPES,
    PRODUCT_ORDER_ID,
    UNARCHIVED_ORCHESTRATION_PLANS_UI_URL,
    UNDEFINED_DATE_VALUE,
    UNDEFINED_LEAD_TIME_VALUE,
} from '../utils/constants.js';
import AsideDetailsCard from './AsideDetailsCard';
import {useAuth} from '../context/AuthContext';
import ShipmentGroupSection from './ShipmentGroupSection.jsx';

export const TYPES = [
    {name: 'Initialized', icon: InitializedSVG},
    {name: 'Acknowledged', icon: AcknowledgedSVG},
    {name: 'InProgress', icon: InProgressSVG},
    {name: 'InDelivery', icon: InDeliverySVG},
    {name: 'Held', icon: HeldSVG},
    {name: 'Aborted', icon: AbortedSVG},
    {name: 'Rejected', icon: RejectedSVG},
    {name: 'Failed', icon: FailedSVG},
    {name: 'Completed', icon: CompletedSVG},
];

const getNodeDisplayName = (node) =>
    node?.relatedProduct?.find(
        (p) => p?.relationshipType === 'delivers'
    )?.productSpecification?.name;

const shouldShowNode = (node, nodeState) =>
    nodeState?.toLowerCase() !== 'initialized' || getNodeDisplayName(node);

// ---------------------------------------------------------------------------
// Sub-components (outside main component to avoid re-creation on render)
// ---------------------------------------------------------------------------

const IconButton = ({onClick, disabled, iconClass, tooltip}) => {
    const button = (
        <button
            type="button"
            className={`btn btn-icon ms-1 ${disabled ? 'btn-icon-disabled' : ''}`}
            onClick={onClick}
            disabled={disabled}
        >
            <em className={`${iconClass} ${disabled ? 'icon-disabled' : ''}`}/>
        </button>
    );

    if (disabled) return button;

    return (
        <BootstrapTooltip title={tooltip} placement="top">
            {button}
        </BootstrapTooltip>
    );
};

IconButton.propTypes = {
    onClick: PropTypes.func.isRequired,
    disabled: PropTypes.bool,
    iconClass: PropTypes.string.isRequired,
    tooltip: PropTypes.string.isRequired,
};

function computeShipmentGroups(nodes) {
    const groupsMap = new Map();
    const regularNodes = [];

    for (const node of nodes) {
        const shipmentProduct = node.relatedProduct?.find(
            (p) => p['@type']?.toLowerCase() === 'shipmentproduct'
        );
        if (shipmentProduct) {
            const key = shipmentProduct.productOrderItemId;
            if (!groupsMap.has(key)) {
                groupsMap.set(key, {
                    id: key,
                    shippingCharacteristics: shipmentProduct.productCharacteristic,
                    nodes: [],
                });
            }
            groupsMap.get(key).nodes.push(node);
        } else {
            regularNodes.push(node);
        }
    }

    return { regularNodes, tangibleGroups: [...groupsMap.values()] };
}

function OrchestrationPlanDetails({dto, error, refreshData}) {
    const [activeTab, setActiveTab] = useState('details');
    const [activeAccordion, setActiveAccordion] = useState(null);
    const [productId, setProductId] = useState(null);
    const [orderId, setOrderId] = useState(null);

    const {canViewProducts, canViewOrders} = useAuth();
    const {featureOrderManagement, featureProductInventory} = useFederationConfig();
    const {navigateTo} = useNavigation();

    const idParam = new URLSearchParams(window.location.search).get('id');
    const tabParam = new URLSearchParams(window.location.search).get('tab');

    const schedule = dto?.orchestrationPlanSchedule;
    const productOrderId = dto?.relatedProductOrder?.id;

    const planDetails = useMemo(() => ({
        [PRODUCT_ORDER_ID]: productOrderId,
        'Plan Id': dto?.id,
        'Received Date': dto?.receivedDate,
        State: dto?.state,
        'Requested Delivery Date': dto?.requestedDeliveryDate,
        'Start Date': schedule?.orderStartDate,
        'Estimated Delivery Lead Time':
            schedule?.estimatedOrderDeliveryLeadTime === UNDEFINED_LEAD_TIME_VALUE
                ? 'undefined'
                : getLeadTime(schedule?.estimatedOrderDeliveryLeadTime),
        'Expected Completion Date':
            schedule?.expectedOrderCompletionDate === UNDEFINED_DATE_VALUE
                ? 'undefined'
                : schedule?.expectedOrderCompletionDate,
        'Actual Start Date': schedule?.actualOrderStartDate,
        'Actual Completion Date': schedule?.actualOrderCompletionDate,
        'Actual Delivery Lead Time': getLeadTime(schedule?.actualOrderDeliveryLeadTime),
        'Contract Name': dto?.relatedContractName,
    }), [dto, schedule, productOrderId]);

    const partyDetails = useMemo(() => ({
        'Party Id': dto?.relatedParty?.[0]?.id,
        'Party Name': dto?.relatedParty?.[0]?.name,
    }), [dto?.relatedParty]);

    const isResolved = useMemo(
        () => isResolvedErrorMessage(dto?.state?.toLowerCase()),
        [dto?.state],
    );

    const getNodeState = useCallback(
        (id) => dto.orchestrationPlanNodes.filter((n) => n.id === id).map((n) => n.state),
        [dto?.orchestrationPlanNodes],
    );

    const getNodeName = useCallback(
        (id) => dto.orchestrationPlanNodes.find((n) => n.id === id)
            ?.relatedProduct?.find((rp) => rp.relationshipType === 'delivers')
            ?.productSpecification?.name,
        [dto?.orchestrationPlanNodes],
    );

    const toggleAccordion = useCallback((index) => {
        setActiveAccordion((prev) => (prev === index ? null : index));
    }, []);

    const getIndexByIdViewDetails = useCallback(
        (id) => {
            const index = dto?.orchestrationPlanNodes?.findIndex((n) => n.id === id);
            if (index >= 0) toggleAccordion(index);
        },
        [dto?.orchestrationPlanNodes, toggleAccordion],
    );

    const handleTabClick = useCallback((tab) => {
        setActiveTab(tab);
        const url = new URL(window.location.href);
        url.searchParams.set('tab', tab);
        window.history.pushState({}, '', url);
    }, []);

    const handleProductClick = useCallback(() => {
        if (productId) {
            navigateTo(`/product-inventory/products-details-page/${productId}?tab=Full+Hierarchy`);
        }
    }, [productId, navigateTo]);

    const handleOrderClick = useCallback(() => {
        if (orderId) {
            navigateTo(`/order-inventory/orders-details-page/${orderId}`);
        }
    }, [orderId, navigateTo]);

    // Sync tab from URL
    useEffect(() => {
        const validTabs = ['details', 'graph', 'schedule'];
        setActiveTab(validTabs.includes(tabParam) ? tabParam : 'details');
    }, [tabParam]);

    // Fetch product and order IDs (runs in background — no loading gate)
    useEffect(() => {
        getIndexByIdViewDetails(idParam);

        if (productOrderId) {
            Promise.all([
                service.getContractProductId(productOrderId),
                service.getOrderId(productOrderId),
            ]).then(([pid, oid]) => {
                if (pid) setProductId(pid);
                if (oid) setOrderId(oid);
            });
        }
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const hasNodes = dto?.orchestrationPlanNodes?.length > 0;
    const isInitialized = dto?.state?.toLowerCase() === 'initialized';
    const showNoData = isInitialized || !hasNodes || error?.message === 'No data found';

    return (
        <>
            <div className="container-fluid">
                <div className="row">
                    <div className="col-12">
                        <nav aria-label="breadcrumb">
                            <ol className="breadcrumb mb-0">
                                <li className="breadcrumb-item">
                                    <a href={UNARCHIVED_ORCHESTRATION_PLANS_UI_URL}>Monitoring</a>
                                </li>
                                <li className="breadcrumb-item active" aria-current="page">
                                    Orchestration Plan Details
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>
            </div>

            <div className="container-fluid py-2">
                <div className="row">
                    <div className="col-12">
                        <h1 className="display-3 mb-1 d-inline-block">Orchestration Plan Details</h1>

                        {canViewOrders && featureOrderManagement && (
                            <IconButton
                                onClick={handleOrderClick}
                                disabled={!orderId}
                                iconClass="icon-order"
                                tooltip="View Order Details"
                            />
                        )}

                        {canViewProducts && featureProductInventory && (
                            <IconButton
                                onClick={handleProductClick}
                                disabled={!productId}
                                iconClass="icon-contract-black"
                                tooltip="View Products"
                            />
                        )}
                    </div>
                </div>

                <div className="row mt-0 g-3 py-1">
                    <div className="col-8">
                        <div className="card mb-3">
                            <div className="card-header py-3">
                                <div className="card-title justify-content-between align-items-center d-flex">
                                    <h3 className="mb-0">Orchestration Nodes Details</h3>
                                    <button
                                        onClick={refreshData}
                                        className="btn btn-link on-black p-0 border-0"
                                        type="button"
                                    >
                                        <em className="icon-reload icon-reload-lg"/>
                                    </button>
                                </div>
                            </div>

                            {showNoData ? (
                                <div className="p-5 text-center">There is no data to display.</div>
                            ) : (
                                <div className="card-body my-3">
                                    {/* Tabs */}
                                    <ul role="tablist" aria-owns="nav-tab1 nav-tab2 nav-tab3" className="nav nav-tabs">
                                        {['details', 'graph', 'schedule'].map((tab) => (
                                            <li key={tab} className="nav-item" role="presentation">
                                                <a
                                                    className={`nav-link ${activeTab === tab ? 'active' : ''}`}
                                                    id={`nav-tab-${tab}`}
                                                    data-bs-toggle="tab"
                                                    href={`#tab-${tab}`}
                                                    role="tab"
                                                    aria-selected={activeTab === tab}
                                                    onClick={() => handleTabClick(tab)}
                                                >
                                                    {tab.charAt(0).toUpperCase() + tab.slice(1)}
                                                </a>
                                            </li>
                                        ))}
                                    </ul>

                                    <div className="tab-content mt-0 border-0 px-0" id="nav-tabs-content">
                                        {/* Details tab */}
                                        <div
                                            className={`tab-pane fade ${activeTab === 'details' ? 'show active' : ''}`}
                                            id="tab-details"
                                            role="tabpanel"
                                        >
                                            <div className="container-xxl">
                                                <div className="row">
                                                    <div className="col-12">
                                                    {(() => {
                                                            const { regularNodes, tangibleGroups } = computeShipmentGroups(dto.orchestrationPlanNodes);
                                                            return (
                                                                <>
                                                                    <div className="accordion py-2" id="accordionExample">
                                                                        {regularNodes.map((node) => {
                                                                            const nodeState = getNodeState(node.id)[0];
                                                                            if (!shouldShowNode(node, nodeState)) return null;

                                                                            const actionType = getNodeActionType(node);
                                                                            const actionIcon = getActionIconClassName(actionType);
            
                                                                            return (
                                                                                (getNodeState(node.id)[0]?.toLowerCase() !== 'initialized' ||
                                                                                    node?.relatedProduct?.find(product => product?.relationshipType === 'delivers')?.productSpecification?.name)
                                                                                && (
                                                                                    <div className="accordion-item" key={node.id}>
                                                                                        <h2 className="accordion-header border-top-0">
                                                                                            <button
                                                                                                className={`accordion-button position-relative ${activeAccordion === node.id ? '' : 'collapsed'}`}
                                                                                                type="button"
                                                                                                onClick={() => toggleAccordion(node.id)}
                                                                                                aria-expanded={activeAccordion === node.id}
                                                                                            >
                                                                                                <div className="shorten-title">
                                                                                                    {node?.relatedProduct?.find(
                                                                                                        (product) =>
                                                                                                            product?.relationshipType === 'delivers'
                                                                                                    )?.productSpecification?.name || 'Order Item'}
                                                                                                </div>
                                                                                                <p className="mb-0 positioned-label gap-1 d-flex align-items-center">
                                                                                                    {actionType && (
                                                                                                        <span className={`tag tag-sm action-type gap-2 ${actionType}`}>
                                                                                                            <em className={`icon-${actionIcon}`}></em>
                                                                                                            {ACTION_TYPES[actionType] ?? 'No Change'}
                                                                                                        </span>
                                                                                                    )}
                                                                                                    {node?.state && (
                                                                                                        <span className={`tag tag-sm status-value ${node.state.toLowerCase()}`}>
                                                                                                            {node.state}
                                                                                                        </span>
                                                                                                    )}
                                                                                                </p>
                                                                                            </button>
                                                                                        </h2>
                                                                                        <div
                                                                                            id={`collapse${node.id}`}
                                                                                            className={`accordion-collapse collapse ${activeAccordion === node.id ? 'show' : ''} scrollable-lg-area`}
                                                                                            data-bs-parent="#accordionExample"
                                                                                        >
                                                                                            <OrchestrationAccordionItem
                                                                                                getNodeState={getNodeState}
                                                                                                getNodeName={getNodeName}
                                                                                                orchestrationDetails={node}
                                                                                                referredType="Node"
                                                                                                />
                                                                                        </div>
                                                                                    </div>
                                                                                ))
                                                    })}
                                                                    </div>
                                                                    {tangibleGroups.map((group, i) => (
                                                                        <ShipmentGroupSection
                                                                            key={group.id}
                                                                            group={group}
                                                                            activeAccordion={activeAccordion}
                                                                            toggleAccordion={toggleAccordion}
                                                                            getNodeState={getNodeState}
                                                                            getNodeName={getNodeName}
                                                                        />
                                                                    ))}
                                                                </>
                                                            );
                                                        })()}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        {/* Graph tab */}
                                        <div
                                            className={`tab-pane fade ${activeTab === 'graph' ? 'show active' : ''}`}
                                            id="tab-graph"
                                            role="tabpanel"
                                        >
                                            <OrchestrationNodeGraph
                                                plan={dto}
                                                setActiveTab={handleTabClick}
                                                getIndexByIdViewDetails={getIndexByIdViewDetails}
                                            />
                                        </div>

                                        {/* Schedule tab */}
                                        <div
                                            className={`tab-pane fade ${activeTab === 'schedule' ? 'show active' : ''}`}
                                            id="tab-schedule"
                                            role="tabpanel"
                                        >
                                            <OrchestrationTimeline plan={dto}/>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        {dto?.errorMessage && (
                            <ErrorMessageBox
                                errorMessages={dto.errorMessage.slice().reverse()}
                                isResolved={isResolved}
                            />
                        )}
                    </div>

                    <div className="col-4">
                        {planDetails['Plan Id'] && (
                            <AsideDetailsCard title="Plan Details" data={planDetails}/>
                        )}
                        {partyDetails['Party Id'] && (
                            <AsideDetailsCard title="Party Details" data={partyDetails}/>
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}

export default OrchestrationPlanDetails;

OrchestrationPlanDetails.propTypes = {
    dto: PropTypes.object,
    error: PropTypes.string,
    refreshData: PropTypes.func.isRequired,
};