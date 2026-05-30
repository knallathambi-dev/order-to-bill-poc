// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import React, {useCallback, useMemo, useState} from "react";
import {formatToLocalDateTime} from "@discobole/common-ui";
import ProductOfferingDetails from "./productDetailsParts/ProductOfferingDetails";
import ProductSpecification from "./productDetailsParts/ProductSpecification";
import ProductOrderItem from "./productDetailsParts/ProductOrderItem";
import RelatedPartyDetails from "./productDetailsParts/RelatedPartyDetails";
import ProductCharacteristicDetails from "./productDetailsParts/ProductCharacteristicDetails";
import BillingAccountDetails from "./productDetailsParts/BillingAccountDetails";
import RelatedProductItemsDetails from "./productDetailsParts/RelatedProductItemsDetails";
import RealizingResourceDetails from "./productDetailsParts/RealizingResourceDetails";
import RealizingServiceDetails from "./productDetailsParts/RealizingServiceDetails";
import ExternalIdentifier from "./productDetailsParts/ExternalIdentifier";
import ProductTerm from "./productDetailsParts/ProductTerm";
import StatusDetails from "./productDetailsParts/StatusDetails";
import {
    CheckboxField,
    ProductPriceSection,
    StatusBadge,
    TableRow,
    TableStructure,
} from "./productDetailsParts/ProductDetailsUtils";

const SECTION_INDEXES = {
    PRODUCT_OFFERING: 0,
    PRODUCT_SPECIFICATION: 1,
    PRODUCT_CHARACTERISTICS: 2,
    RELATED_PARTY: 3,
    BILLING_ACCOUNT: 4,
    PRODUCT_ORDER_ITEM: 5,
    PRODUCT_PRICE: 6,
    REALIZING_RESOURCE: 7,
    STATUS: 8,
    OPERATIONAL_STATUS: 9,
    RELATED_PRODUCTS: 10,
    EXTERNAL_IDENTIFIER: 11,
    PRODUCT_TERM: 12,
    REALIZING_SERVICE: 13,
};

const EXPANDED_INDEXES = [0, 1, 3, 5];
const SECTION_COUNT = Object.keys(SECTION_INDEXES).length;

const createInitialSections = () =>
    Array.from({length: SECTION_COUNT}, (_, index) => ({
        index,
        isExpanded: EXPANDED_INDEXES.includes(index),
    }));

const isPhysicalOrShipmentProduct = (type) =>
    type === "PhysicalProduct" || type === "ShipmentProduct";

const ProductsBasicDetails = ({product}) => {
    const [sections, setSections] = useState(createInitialSections);

    const toggleAccordion = useCallback((index) => {
        setSections((prev) =>
            prev.map((s) => (s.index === index ? {...s, isExpanded: !s.isExpanded} : s))
        );
    }, []);

    const showSerialNumber = useMemo(
        () => product && isPhysicalOrShipmentProduct(product["@type"]),
        [product]
    );

    const statusClass = useMemo(
        () => `fw-bold ${product?.status === "active" ? "text-success" : "text-danger"}`,
        [product?.status]
    );

    if (!product) {
        return <div className="text-center text-muted">No product data available</div>;
    }

    return (
        <div>
            <div className="row mt-0 g-3 py-1">
                <div className="col-md-12">
                    {/* Main info card */}
                    <div className="card mb-3">
                        <div className="card-body">
                            <div className="row">
                                <div className="col-md-6">
                                    <TableStructure>
                                        <TableRow label="Id" value={product.id || "_"}/>
                                        <TableRow label="Name" value={product.name || "_"}/>
                                        <TableRow label="Is Bundle" value={<CheckboxField isChecked={product.isBundle}/>}/>
                                        <TableRow
                                            label="Status"
                                            value={<p className="mb-0"><StatusBadge status={product.status}/></p>}
                                            className={statusClass}
                                        />
                                        <TableRow label="Order Date" value={formatToLocalDateTime(product.orderDate) || "_"} className="fw-bold text-break"/>
                                        <TableRow label="Start Date" value={formatToLocalDateTime(product.startDate) || "_"}/>
                                        <TableRow label="Termination Date" value={formatToLocalDateTime(product.terminationDate) || "_"} className="fw-bold bottom"/>
                                    </TableStructure>
                                </div>
                                <div className="col-md-6">
                                    <TableStructure>
                                        <TableRow label="Type" value={product["@type"] || "_"}/>
                                        <TableRow label="Description" value={product.description || "_"}/>
                                        <TableRow label="Is Customer Visible" value={<CheckboxField isChecked={product.isCustomerVisible}/>}/>
                                        <TableRow
                                            label="Operational Status"
                                            value={<p className="mb-0"><StatusBadge status={product.operationalStatus}/></p>}
                                        />
                                        <TableRow label="Creation Date" value={formatToLocalDateTime(product.creationDate) || "_"}/>
                                        <TableRow label="Last Update Date" value={formatToLocalDateTime(product.lastUpdateDate) || "_"}/>
                                        <tr><td colSpan="2"><p/></td></tr>
                                    </TableStructure>
                                </div>
                                {showSerialNumber && (
                                    <div className="col-md-12">
                                        <TableStructure>
                                            <TableRow label="Product Serial Number" value={product.productSerialNumber || "_"}/>
                                        </TableStructure>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {product.productOffering && (
                        <ProductOfferingDetails
                            sections={sections}
                            onClick={() => toggleAccordion(SECTION_INDEXES.PRODUCT_OFFERING)}
                            productOffering={product.productOffering}
                        />
                    )}

                    {product.productSpecification && (
                        <ProductSpecification
                            sections={sections}
                            onClick={() => toggleAccordion(SECTION_INDEXES.PRODUCT_SPECIFICATION)}
                            productSpecification={product.productSpecification}
                        />
                    )}

                    <ProductOrderItem
                        sections={sections}
                        onClick={() => toggleAccordion(SECTION_INDEXES.PRODUCT_ORDER_ITEM)}
                        productOrderItem={product.productOrderItem}
                    />

                    <RelatedPartyDetails
                        sections={sections}
                        onClick={() => toggleAccordion(SECTION_INDEXES.RELATED_PARTY)}
                        relatedParty={product.relatedParty}
                    />

                    <ProductPriceSection
                        sections={sections}
                        sectionIndex={SECTION_INDEXES.PRODUCT_PRICE}
                        toggleAccordion={toggleAccordion}
                        productPrice={product.productPrice}
                    />

                    {product.productCharacteristic && (
                        <ProductCharacteristicDetails
                            sections={sections}
                            onClick={() => toggleAccordion(SECTION_INDEXES.PRODUCT_CHARACTERISTICS)}
                            productCharacteristic={product.productCharacteristic}
                        />
                    )}

                    <div className="row d-flex">
                        <div className="col-md-6">
                            <div className="row">
                                <BillingAccountDetails
                                    sections={sections}
                                    onClick={() => toggleAccordion(SECTION_INDEXES.BILLING_ACCOUNT)}
                                    billingAccount={product.billingAccount}
                                />

                                <RelatedProductItemsDetails
                                    sections={sections}
                                    onClick={() => toggleAccordion(SECTION_INDEXES.RELATED_PRODUCTS)}
                                    productRelationship={product.productRelationship}
                                />

                                {product.externalIdentifier && (
                                    <ExternalIdentifier
                                        sections={sections}
                                        onClick={() => toggleAccordion(SECTION_INDEXES.EXTERNAL_IDENTIFIER)}
                                        externalIdentifier={product.externalIdentifier}
                                    />
                                )}

                                <ProductTerm
                                    sections={sections}
                                    onClick={() => toggleAccordion(SECTION_INDEXES.PRODUCT_TERM)}
                                    productTerm={product.productTerm}
                                />
                            </div>
                        </div>

                        <div className="col-md-6">
                            <div className="row">
                                <RealizingResourceDetails
                                    sections={sections}
                                    onClick={() => toggleAccordion(SECTION_INDEXES.REALIZING_RESOURCE)}
                                    realizingResource={product.realizingResource}
                                />

                                <RealizingServiceDetails
                                    sections={sections}
                                    onClick={() => toggleAccordion(SECTION_INDEXES.REALIZING_SERVICE)}
                                    realizingService={product.realizingService}
                                />

                                <StatusDetails
                                    title="Status Change History"
                                    section={sections[SECTION_INDEXES.STATUS]}
                                    toggleAccordion={toggleAccordion}
                                    data={product.statusChange}
                                />

                                <StatusDetails
                                    title="Operational Status Change History"
                                    section={sections[SECTION_INDEXES.OPERATIONAL_STATUS]}
                                    toggleAccordion={toggleAccordion}
                                    data={product.operationalStatusChange}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

ProductsBasicDetails.propTypes = {
    product: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        isBundle: PropTypes.bool,
        status: PropTypes.string,
        orderDate: PropTypes.string,
        startDate: PropTypes.string,
        terminationDate: PropTypes.string,
        "@type": PropTypes.string,
        description: PropTypes.string,
        isCustomerVisible: PropTypes.bool,
        operationalStatus: PropTypes.string,
        creationDate: PropTypes.string,
        lastUpdateDate: PropTypes.string,
        productSerialNumber: PropTypes.string,
        productOffering: PropTypes.object,
        productSpecification: PropTypes.object,
        productOrderItem: PropTypes.array,
        relatedParty: PropTypes.array,
        productCharacteristic: PropTypes.array,
        billingAccount: PropTypes.object,
        productRelationship: PropTypes.array,
        externalIdentifier: PropTypes.array,
        productTerm: PropTypes.array,
        productPrice: PropTypes.array,
        realizingResource: PropTypes.array,
        realizingService: PropTypes.array,
        statusChange: PropTypes.array,
        operationalStatusChange: PropTypes.array,
    }),
};

export default ProductsBasicDetails;