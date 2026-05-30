// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useMemo} from 'react';
import PropTypes from 'prop-types';
import AccordionCardBody from './AccordionCardBody';
import AccordionCardSection from './AccordionCardSection';
import AccordionCardRow from './AccordionCardRow';
import AccordionCardCharacteristicRow from './AccordionCardCharacteristicRow';
import {getLeadTime, getRelatedProductOrderItem, isResolvedErrorMessage} from '../service/orchestrationUtils.js';
import {ErrorMessageBox, formatToLocalDateTime} from '@discobole/common-ui';
import {
    CHARACTERISTIC_ADDRESS_TYPE,
    CHARACTERISTIC_VALIDITY_TYPE,
    UNDEFINED_DATE_VALUE,
    UNDEFINED_LEAD_TIME_VALUE,
} from '../utils/constants.js';

const getCharacteristicValue = ({value, unitOfMeasure}) => {
    if (value && unitOfMeasure) return `${value} ${unitOfMeasure}`;
    if (value) return typeof value === 'object' ? JSON.stringify(value) : value;
    return value;
};

const getValidityValue = ({validTo, validFrom}) => {
    const entries = [];
    if (validFrom) entries.push({key: 'Valid From', value: formatToLocalDateTime(validFrom)});
    if (validTo) entries.push({key: 'Valid To', value: formatToLocalDateTime(validTo)});
    return entries.length ? entries : undefined;
};

const buildTimeInfo = (schedule) => {
    if (!schedule) return {};

    const estimatedLeadTime = schedule.estimatedOrderItemDeliveryLeadTime;
    const expectedCompletion = schedule.expectedOrderItemCompletionDate;

    return {
        'Start Date': schedule.orderItemStartDate,
        'Estimated Delivery Lead Time':
            estimatedLeadTime === UNDEFINED_LEAD_TIME_VALUE
                ? 'undefined'
                : getLeadTime(estimatedLeadTime),
        'Expected Completion Date':
            expectedCompletion === UNDEFINED_DATE_VALUE
                ? 'undefined'
                : expectedCompletion,
        'Actual Start Date': schedule.actualOrderItemStartDate,
        'Actual Completion Date': schedule.actualOrderItemCompletionDate,
        'Actual Delivery Lead Time': getLeadTime(schedule.actualOrderItemDeliveryLeadTime),
    };
};

const CharacteristicSection = ({characteristic, index}) => {
    const {name, value, ['@type']: type} = characteristic;

    if (type === CHARACTERISTIC_ADDRESS_TYPE) {
        const {city, country, streetName, postcode} = characteristic;
        if (city && country && streetName && postcode) {
            return (
                <AccordionCardCharacteristicRow
                    key={name + index}
                    name={name}
                    value={`${streetName}, ${city}, ${country} - ${postcode}`}
                    type={type}
                />
            );
        }
    }

    if (!name || !value) return null;

    if (type === CHARACTERISTIC_VALIDITY_TYPE) {
        const validityValue = getValidityValue(characteristic.value);
        if (validityValue) {
            return (
                <AccordionCardCharacteristicRow
                    key={name + index}
                    name={name}
                    value={validityValue}
                    type={type}
                />
            );
        }
    }

    return (
        <AccordionCardCharacteristicRow
            key={name + index}
            name={name}
            value={getCharacteristicValue(characteristic)}
            type={type}
        />
    );
};

const ActionTag = ({state}) => {
    const tagMap = {
        noChange: {className: '', icon: 'anti_spam', label: 'No Change'},
        modify: {className: 'blue', icon: 'done_modifier', label: 'Modify'},
        add: {className: 'green', icon: 'modifier_add', label: 'Add'},
        terminate: {className: 'red', icon: 'Modifier_delete', label: 'Terminate'},
    };
    const tag = tagMap[state];
    if (!tag) return null;

    return (
        <span className={`tag tag-sm action-type ${tag.className}`}>
            <em className={`icon-${tag.icon} action-icon`}/> {tag.label}
        </span>
    );
};

function OrchestrationAccordionItem({
                                        getNodeState,
                                        getNodeName,
                                        orchestrationDetails,
                                        referredType,
                                        hideShipmentCharacteristics = false
                                    }) {
    const errorMessages = useMemo(
        () => orchestrationDetails?.errorMessage?.slice().reverse(),
        [orchestrationDetails?.errorMessage],
    );

    const isResolved = useMemo(
        () => isResolvedErrorMessage(orchestrationDetails?.state?.toLowerCase()),
        [orchestrationDetails?.state],
    );

    const timeInfo = useMemo(
        () => buildTimeInfo(orchestrationDetails?.orchestrationNodeSchedule),
        [orchestrationDetails?.orchestrationNodeSchedule],
    );

    const timeInfoEntries = useMemo(
        () => Object.entries(timeInfo).filter(([, v]) => v),
        [timeInfo],
    );

    return (
        <div className="accordion-body">
            {/* Basic Details */}
            <div className="card border-1 mb-3">
                <div className="card-body">
                    <h5 className="card-title">Basic Details</h5>
                    <div className="table-responsive">
                        <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                            <tbody className="fw-semibold">
                            <AccordionCardRow
                                title={`${referredType} Id`}
                                value={orchestrationDetails?.id}
                            />
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            {/* Error (first unresolved) */}
            {errorMessages && !isResolved && (
                <ErrorMessageBox errorMessages={[errorMessages[0]]} isResolved={false}/>
            )}

            {/* Product details (delivers, non-shipment) */}
            {orchestrationDetails?.relatedProduct?.map((product, index) => (
                <React.Fragment key={product.relationshipType + product.productOrderItemId + index}>
                    {product.relationshipType === 'delivers' &&
                        product['@type']?.toLowerCase() !== 'shipmentproduct' && (
                            <AccordionCardBody>
                                {/* Product details card */}
                                <div className="card border-1 mb-3">
                                    <div className="card-body">
                                        <div className="d-flex justify-content-between mb-2">
                                            <h5 className="card-title">Product Details</h5>
                                            <p className="mb-0">
                                                <ActionTag state={orchestrationDetails?.state}/>
                                            </p>
                                        </div>
                                        <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                                            <tbody className="fw-semibold">
                                            {product.id && (
                                                <AccordionCardRow title="Product Id" value={product.id}/>
                                            )}
                                            <AccordionCardRow
                                                title="Relationship type"
                                                value={product.relationshipType}
                                            />
                                            {product.realisingService?.length > 0 && (
                                                <AccordionCardRow
                                                    title="Realizing service Id"
                                                    value={product.realisingService.map((s) => (
                                                        <span className="tag tag-sm" key={s.id}>
                                                                {s.id}
                                                            </span>
                                                    ))}
                                                />
                                            )}
                                            <AccordionCardRow
                                                title="Product Order Item Id"
                                                value={getRelatedProductOrderItem(
                                                    orchestrationDetails,
                                                    product.relationshipType,
                                                )}
                                            />
                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                {/* Product Specification */}
                                <AccordionCardSection title="Product Specification">
                                    <AccordionCardRow
                                        title="Product Specification Id"
                                        value={product.productSpecification?.id}
                                    />
                                    <AccordionCardRow
                                        title="Product Specification Name"
                                        value={product.productSpecification?.name}
                                    />
                                    <AccordionCardRow
                                        title="isInstallable"
                                        value={product.isInstallable ? 'True' : 'False'}
                                    />
                                    <AccordionCardRow
                                        title="Quantity"
                                        value={
                                            orchestrationDetails?.relatedProductOrderItem?.find(
                                                (item) => item.quantity,
                                            )?.quantity || '-'
                                        }
                                    />
                                </AccordionCardSection>

                                {/* Product Characteristics */}
                                {product.productCharacteristic?.length > 0 && (
                                    <AccordionCardSection title="Product Characteristics">
                                        {product.productCharacteristic.map((info, i) =>
                                            <CharacteristicSection
                                                key={info.name + i}
                                                characteristic={info}
                                                index={i}
                                            />
                                        )}
                                    </AccordionCardSection>
                                )}

                                {/* Shipment Characteristics */}
                                {!hideShipmentCharacteristics && orchestrationDetails?.relatedProduct?.map((product, spIdx) => (
                                    <React.Fragment key={spIdx}>
                                        {product['@type']?.toLowerCase() === 'shipmentproduct' &&
                                            product.productCharacteristic?.length > 0 && (
                                                <AccordionCardSection title="Shipment Characteristics">
                                                    {product.productCharacteristic.map((info, pi) => (
                                                        <CharacteristicSection
                                                            key={info.name + pi}
                                                            characteristic={info}
                                                            index={pi}
                                                        />
                                                    ))}
                                                </AccordionCardSection>
                                            )}
                                    </React.Fragment>
                                ))}

                                {/* Shipping Order Details */}
                                {orchestrationDetails?.relatedSupplyChainOrderItem && (
                                    <AccordionCardSection title="Shipping Order Details">
                                        <AccordionCardRow
                                            title="Shipping Order Id"
                                            value={orchestrationDetails.relatedSupplyChainOrderItem.id ?? '-'}
                                        />
                                        <AccordionCardRow
                                            title="Shipping Order Item Id"
                                            value={orchestrationDetails.relatedSupplyChainOrderItem.orderItemId ?? '-'}
                                        />
                                    </AccordionCardSection>
                                )}

                                {/* Service Specification */}
                                {product.productSpecification?.serviceSpecification?.length > 0 && (
                                    <AccordionCardSection title="Service Specification">
                                        {product.productSpecification.serviceSpecification.map((info, si) => (
                                            <React.Fragment key={info?.id + info?.name}>
                                                <AccordionCardRow
                                                    title="Service Specification Id"
                                                    value={info.id ?? '-'}
                                                />
                                                <AccordionCardRow
                                                    title="Service Specification Name"
                                                    value={info.name ?? '-'}
                                                />
                                                <AccordionCardRow
                                                    title="Service Specification Version"
                                                    value={info.version ?? '-'}
                                                />
                                                {si + 1 !== product.productSpecification.serviceSpecification.length && (
                                                    <tr className="border-0">
                                                        <td></td>
                                                    </tr>
                                                )}
                                            </React.Fragment>
                                        ))}
                                    </AccordionCardSection>
                                )}
                            </AccordionCardBody>
                        )}
                </React.Fragment>
            ))}

            {/* Service Order Details */}
            {orchestrationDetails?.relatedServiceOrder &&
                Object.keys(orchestrationDetails.relatedServiceOrder).length > 0 && (
                    <AccordionCardBody>
                        <AccordionCardSection title="Service Order Details">
                            <AccordionCardRow
                                title="Service Order Id"
                                value={orchestrationDetails.relatedServiceOrder.id ?? '-'}
                            />
                            <AccordionCardRow
                                title="Service Order Item Id"
                                value={orchestrationDetails.relatedServiceOrder.orderItemId ?? '-'}
                            />
                            <AccordionCardRow
                                title="Service Factory"
                                value={orchestrationDetails.relatedServiceOrder.serviceOrderManagementRef ?? '-'}
                            />
                        </AccordionCardSection>
                    </AccordionCardBody>
                )}

            {/* Prerequisites */}
            {orchestrationDetails?.relatedOrchestrationPlanNode?.length > 0 && (
                <AccordionCardBody>
                    <AccordionCardSection title="Prerequisites">
                        {orchestrationDetails.relatedOrchestrationPlanNode.map((n, index) => (
                            <React.Fragment key={n.relatedNodeId + index}>
                                <AccordionCardRow title="Node Id" value={n.relatedNodeId ?? '-'}/>
                                <AccordionCardRow
                                    title="Node Name"
                                    value={getNodeName(n.relatedNodeId) ?? '-'}
                                />
                                <AccordionCardRow
                                    title="Node State"
                                    value={
                                        <p className="mb-0">
                                            <span
                                                className={`tag tag-sm status-value ${getNodeState(n.relatedNodeId)[0]?.toLowerCase()}`}
                                            >
                                                {getNodeState(n.relatedNodeId) ?? '-'}
                                            </span>
                                        </p>
                                    }
                                />
                                {index + 1 < orchestrationDetails.relatedOrchestrationPlanNode.length && (
                                    <tr className="border-0">
                                        <td></td>
                                    </tr>
                                )}
                            </React.Fragment>
                        ))}
                    </AccordionCardSection>
                </AccordionCardBody>
            )}

            {/* Related Products (non-delivers, non-migratedFrom) */}
            {orchestrationDetails?.relatedProduct?.map((product, index) => (
                <React.Fragment key={product.relationshipType + product.productOrderItemId + index}>
                    {product.relationshipType !== 'delivers' &&
                        product.relationshipType !== 'migratedFrom' && (
                            <AccordionCardBody>
                                <AccordionCardSection title="Related Product">
                                    {product.id && (
                                        <AccordionCardRow title="Product Id" value={product.id}/>
                                    )}
                                    <AccordionCardRow
                                        title="Relationship Type"
                                        value={product.relationshipType ?? '-'}
                                    />
                                    {product.relationshipType && (
                                        <AccordionCardRow
                                            title="isInstallable"
                                            value={product.isInstallable ? 'True' : 'False'}
                                        />
                                    )}
                                    {product.productSpecification && (
                                        <AccordionCardRow
                                            title="Product Specification Id"
                                            value={product.productSpecification?.id ?? '-'}
                                        />
                                    )}
                                </AccordionCardSection>
                            </AccordionCardBody>
                        )}

                    {product.relationshipType === 'migratedFrom' && (
                        <AccordionCardBody>
                            <AccordionCardSection title="Related Migrated Product">
                                <AccordionCardRow
                                    title="Product Id"
                                    value={product.id ?? '-'}
                                />
                                <AccordionCardRow title="Relationship Type" value="Migrated from"/>
                                {product.productOrderItemId && (
                                    <AccordionCardRow
                                        title="Product Order Item Id"
                                        value={getRelatedProductOrderItem(
                                            orchestrationDetails,
                                            product.relationshipType,
                                        )}
                                    />
                                )}
                            </AccordionCardSection>
                        </AccordionCardBody>
                    )}
                </React.Fragment>
            ))}

            {/* Time Info */}
            {timeInfoEntries.length > 0 && (
                <AccordionCardBody>
                    <AccordionCardSection title="Time Info">
                        {timeInfoEntries.map(([key, value]) => (
                            <AccordionCardRow key={key} title={key} value={value}/>
                        ))}
                    </AccordionCardSection>
                </AccordionCardBody>
            )}

            {/* Resolved / remaining errors */}
            {errorMessages && (isResolved || errorMessages.length > 1) && (
                <ErrorMessageBox
                    errorMessages={isResolved ? errorMessages : errorMessages.slice(1)}
                    isResolved={true}
                />
            )}
        </div>
    );
}

export default OrchestrationAccordionItem;

OrchestrationAccordionItem.propTypes = {
    getNodeState: PropTypes.func.isRequired,
    getNodeName: PropTypes.func.isRequired,
    orchestrationDetails: PropTypes.object.isRequired,
    referredType: PropTypes.string.isRequired,
    hideShipmentCharacteristics: PropTypes.bool
};