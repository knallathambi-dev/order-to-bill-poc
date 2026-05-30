// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo, useState} from "react";
import PropTypes from "prop-types";
import {useParams} from "react-router-dom";
import {LoadingIndicator} from "@discobole/common-ui";
import PartyDetails from "./PartyDetails";
import ShippingDetails from "./ShippingDetails";
import Breadcrumb from "./Breadcrumb";
import OrderHeader from "./OrderHeader";
import OrderInfo from "./OrderInfo";
import OrderItems from "./OrderItems";
import {byMigrateToThenName, extractShippingDetails, isContractType} from "./utils/orderUtils.js";
import api from "../../service/OrderInventoryAPI.js";

const isAtomic = (item) => item?.productOffering?.["@type"] === "AtomicProductOffering";
const isBundle = (item) => item?.productOffering?.["@type"] === "BundleProductOffering";
const isDeleting = (action) => (action ?? "").toLowerCase() === "delete";

const shapeAtomic = (item) => ({
    id: item.id,
    name: item.productOffering.name,
    characteristics: item.product?.productCharacteristic ?? [],
    state: item.state,
    action: item.action,
    itemTerm: item.itemTerm,
    itemPrice: item.itemPrice ?? null,
    productOrderItemRelationship: item.productOrderItemRelationship,
});

const shapeContract = (item) => ({
    id: item.id,
    name: item.productOffering?.name,
    state: item.state,
    action: item.action,
    itemTerm: item.itemTerm,
    itemPrice: item.itemPrice,
    productOrderItemRelationship: item.productOrderItemRelationship,
});

const buildOrderItems = (productOrderItems) => {
    if (!productOrderItems?.length) return {items: [], contractItems: []};

    const getParentsOf = (child) => {
        if (!child) return [];

        const parentSide = productOrderItems.filter((parent) =>
            parent.productOrderItemRelationship?.some(
                (rel) => rel.relationshipType === "bundles" && rel.id === child.id
            )
        );

        const childSide = (child.productOrderItemRelationship ?? [])
            .filter((rel) => rel.relationshipType === "isChild")
            .map((rel) => productOrderItems.find((it) => it.id === rel.id))
            .filter(Boolean);

        const map = new Map();
        [...parentSide, ...childSide].forEach((p) => map.set(p.id, p));
        return [...map.values()];
    };

    const choosePreferredParent = (parents) => {
        if (!parents?.length) return null;
        return parents.find((p) => !isDeleting(p.action)) ?? parents[0];
    };

    const preferredParentByAtomicId = productOrderItems
        .filter(isAtomic)
        .reduce((acc, atomic) => {
            const preferred = choosePreferredParent(getParentsOf(atomic));
            if (preferred) acc[atomic.id] = preferred.id;
            return acc;
        }, {});

    const processBundleItem = (bundleItem) => {
        const bundleRels = (bundleItem.productOrderItemRelationship ?? [])
            .filter((rel) => rel.relationshipType === "bundles");

        const children = bundleRels
            .map((rel) => {
                const related = productOrderItems.find((item) => item.id === rel.id);
                if (isBundle(related)) return processBundleItem(related);
                if (isAtomic(related)) {
                    const preferredId = preferredParentByAtomicId[related.id];
                    if (!preferredId || preferredId === bundleItem.id) {
                        return shapeAtomic(related);
                    }
                }
                return null;
            })
            .filter(Boolean);

        return {
            id: bundleItem.id,
            name: bundleItem.productOffering.name,
            state: bundleItem.state,
            action: bundleItem.action,
            itemTerm: bundleItem.itemTerm,
            itemPrice: bundleItem.itemPrice ?? null,
            productOrderItemRelationship: bundleItem.productOrderItemRelationship,
            children,
        };
    };

    const isTopLevelBundle = (item) =>
        isBundle(item) &&
        !productOrderItems.some((parent) =>
            parent.productOrderItemRelationship?.some(
                (rel) => rel.id === item.id && rel.relationshipType === "bundles"
            )
        );

    const isStandaloneAtomic = (item) =>
        isAtomic(item) && getParentsOf(item).length === 0;

    const wrapAtomic = (item) => {
        const shaped = shapeAtomic(item);
        return {...shaped, children: [shaped]};
    };

    const contractOrderItems = productOrderItems.filter(isContractType);

    const contractChildren = contractOrderItems.flatMap((contract) => {
        const rels = (contract.productOrderItemRelationship ?? [])
            .filter((rel) => rel.relationshipType === "bundles");

        const bundles = rels
            .map((rel) => productOrderItems.find((item) => item.id === rel.id))
            .filter(isBundle)
            .map(processBundleItem);

        const atomics = productOrderItems
            .filter((item) =>
                isAtomic(item) &&
                contract.productOrderItemRelationship?.some((rel) => rel.id === item.id)
            )
            .filter((item) => {
                const preferredId = preferredParentByAtomicId[item.id];
                return !preferredId || preferredId === contract.id;
            })
            .map(wrapAtomic);

        return [...bundles, ...atomics];
    });

    const standaloneBundles = productOrderItems
        .filter(isTopLevelBundle)
        .map(processBundleItem)
        .sort(byMigrateToThenName);

    const standaloneAtomics = productOrderItems
        .filter(isStandaloneAtomic)
        .map(wrapAtomic)
        .sort(byMigrateToThenName);

    return {
        items: [...contractChildren, ...standaloneBundles, ...standaloneAtomics],
        contractItems: contractOrderItems.map(shapeContract).sort(byMigrateToThenName),
    };
};

const OrderDetails = ({data}) => {
    const {id} = useParams();
    const [orderData, setOrderData] = useState(data);
    const [loading, setLoading] = useState(false);
    const [openAccordions, setOpenAccordions] = useState({});

    const {items, contractItems} = useMemo(
        () => buildOrderItems(orderData.productOrderItem),
        [orderData]
    );

    const atomicItemsCount = useMemo(
        () => (orderData.productOrderItem ?? []).filter(isAtomic).length,
        [orderData]
    );

    const shippingDetails = useMemo(
        () => extractShippingDetails(orderData),
        [orderData]
    );

    const handleReload = useCallback(async () => {
        setLoading(true);
        try {
            setOrderData(await api.getProductOrder(id));
        } catch (err) {
            console.error("Failed to reload data:", err);
        } finally {
            setLoading(false);
        }
    }, [id]);

    const toggleAccordion = useCallback((index) => {
        setOpenAccordions((prev) => ({...prev, [index]: !prev[index]}));
    }, []);

    if (loading) return <LoadingIndicator/>;

    return (
        <div>
            <div className="row">
                <div className="col-12">
                    <Breadcrumb/>
                    <OrderHeader
                        orderId={orderData.id}
                        orderState={orderData.state}
                        handleReload={handleReload}
                    />
                    <OrderInfo
                        order={orderData}
                        creationDate={orderData.creationDate}
                        requestedCompletionDate={orderData.requestedCompletionDate}
                        atomicItemsCount={atomicItemsCount}
                        orderTotalPrice={orderData.orderTotalPrice}
                        orderState={orderData.state}
                    />
                </div>
            </div>
            <div className="row mt-4">
                <OrderItems
                    items={items}
                    contractItems={contractItems}
                    openAccordions={openAccordions}
                    toggleAccordion={toggleAccordion}
                />
                <div className="col-md-5 mb-3">
                    <PartyDetails party={orderData.relatedParty[0]}/>
                </div>
                <div className="col-md-7 mb-3">
                    {shippingDetails && <ShippingDetails shippingDetails={shippingDetails}/>}
                </div>
            </div>
        </div>
    );
};

OrderDetails.propTypes = {
    data: PropTypes.object.isRequired,
};

export default OrderDetails;