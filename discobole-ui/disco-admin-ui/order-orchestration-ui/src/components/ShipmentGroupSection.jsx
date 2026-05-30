// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import { useState } from 'react';
import PropTypes from 'prop-types';
import { ACTION_TYPES } from '../utils/constants.js';
import { getActionIconClassName, getNodeActionType } from '../service/orchestrationUtils.js';
import OrchestrationAccordionItem from './OrchestrationNodeAccordionItem';
import AccordionCardRow from './AccordionCardRow';

function ShipmentGroupSection({ group, activeAccordion, toggleAccordion, getNodeState, getNodeName }) {
    const [isShippingExpanded, setIsShippingExpanded] = useState(false);

    return (
        <div className="card bg-body-tertiary border-1 mt-2 mb-3">
            <div className='card-body py-2'>
                <div className="accordion">
                    <div className="accordion-item border-0 bg-transparent">
                        <h2 className="accordion-header border-top-0">
                            <button
                                type="button"
                                className={`accordion-button position-relative bg-body-tertiary ${isShippingExpanded ? '' : 'collapsed'}`}
                                onClick={() => setIsShippingExpanded((prev) => !prev)}
                                aria-expanded={isShippingExpanded}
                            >
                                <div className="shorten-title">
                                    <h5 className="card-title mb-0">Shipment Group</h5>
                                </div>
                                <p className="mb-0 positioned-label gap-1 d-flex align-items-center">
                                    <span className="tag tag-sm">{group.nodes.length} items</span>
                                </p>
                            </button>
                        </h2>
                        {isShippingExpanded && group.shippingCharacteristics?.length > 0 && (
                            <div className="accordion-body p-0">
                                <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                                    <thead>
                                        <tr>
                                            <th scope="col" className="visually-hidden">Shipping Group</th>
                                        </tr>
                                    </thead>
                                    <tbody className="fw-semibold">
                                        <div className='card border-1 my-2'>
                                            <div className='card-body p-3 pb-2'>
                                                <h5 className="card-title">Shipping Characteristics</h5>
                                                <table className='table align-middle table-row-bordered mb-0 fs-6 gy-5'>
                                                    <thead>
                                                        <tr>
                                                            <th scope="col" className="visually-hidden">Name</th>  
                                                            <th scope="col" className="visually-hidden">Value</th> 
                                                        </tr>
                                                    </thead>
                                                    {group.shippingCharacteristics.map((characteristic, i) => (
                                                        <AccordionCardRow
                                                            key={characteristic.name + i}
                                                            title={characteristic.name ?? '-'}
                                                            value={characteristic.value ?? '-'}
                                                        />
                                                    ))}
                                                </table>
                                            </div>
                                        </div>
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <hr className="m-0 border-1 border-light" />
            <div className='card border-0'>
                <div className='card-body'>

                    <div className="accordion mb-2" id={`accordionGroup-${group.nodes[0]?.id}`}>
                        {group.nodes.map((node) => {
                            const actionType = getNodeActionType(node);
                            const nodeName = node.relatedProduct?.find(
                                (p) =>
                                    p.relationshipType === 'delivers'
                            )?.productSpecification?.name;

                            return (
                                <div className="accordion-item" key={node.id}>
                                    <h2 className="accordion-header border-top-0">
                                        <button
                                            className={`accordion-button position-relative ${activeAccordion === node.id ? '' : 'collapsed'}`}
                                            type="button"
                                            onClick={() => toggleAccordion(node.id)}
                                            aria-expanded={activeAccordion === node.id}
                                        >
                                            <div className="shorten-title">
                                                {nodeName || 'Order Item'}
                                            </div>
                                            <p className="mb-0 positioned-label gap-1 d-flex align-items-center">
                                                {actionType && (
                                                    <span className={`tag tag-sm action-type gap-2 ${actionType}`}>
                                                        <em className={`icon-${getActionIconClassName(actionType)}`}></em>
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
                                        className={`accordion-collapse collapse ${activeAccordion === node.id ? 'show' : ''} scrollable-lg-area`}
                                    >
                                        <OrchestrationAccordionItem
                                            getNodeState={getNodeState}
                                            getNodeName={getNodeName}
                                            orchestrationDetails={node}
                                            referredType="Node"
                                            hideShipmentCharacteristics
                                        />
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ShipmentGroupSection;

ShipmentGroupSection.propTypes = {
    group: PropTypes.shape({
        shippingCharacteristics: PropTypes.array,
        nodes: PropTypes.arrayOf(PropTypes.object).isRequired,
    }).isRequired,
    activeAccordion: PropTypes.string,
    toggleAccordion: PropTypes.func.isRequired,
    getNodeState: PropTypes.func.isRequired,
    getNodeName: PropTypes.func.isRequired,
};
