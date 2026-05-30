// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {calculateItemPricing} from "../../../utlis/helpers";
import {
    ATOMIC_TYPE,
    BUNDLE_TYPE,
    CONTRACT_TYPE,
    SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE
} from "../../../utlis/constants";
import {fetchOrder} from "../../../services";
import apiClient from "../../../services/api/apiClient";

const productCatalogURL = process.env.REACT_APP_PRODUCT_CATALOG_URL;
const productInventoryURL = process.env.REACT_APP_PRODUCT_INVENTORY_URL;

// Utility to fetch bundled product offerings using a contract ID
const fetchBundledOfferingsByContractId = async (contractId) => {
    try {
        const response = await apiClient.get(`${productCatalogURL}/${contractId}`);
        return response.data.bundledProductOffering || [];
    } catch (error) {
        console.error(`Error fetching bundled offerings for contract ID: ${contractId}`, error);
        return [];
    }
};

const isRelatedToShipping = (shippingItem, productOrderItem) =>
    shippingItem?.productOrderItemRelationship?.some(
        (rel) => rel.id === productOrderItem.id && rel.relationshipType === "requires"
    );

const mergeCharacteristics = (productOrderItem, shippingItem) => {
    productOrderItem.product.productCharacteristic = [
        ...(productOrderItem.product.productCharacteristic || []),
        ...(shippingItem.product.productCharacteristic || []),
    ];
};

// Check if a product order item is related to a shipping item
const checkIfRelatedToShipping = (shippingItem, productOrderItem) =>
    shippingItem?.productOrderItemRelationship?.some(
        (rel) => rel.id === productOrderItem.id && rel.relationshipType === "requires"
    );

// Recursive function to classify atomic product offerings into categories
const classifyItemsRecursively = async (
    offerings,
    productOrderItems,
    shippingItem,
    classifiedItemsSet = new Set()
) => {
    const mandatoryItems = [];
    const optionalItems = [];
    const devicesItems = [];

    for (const offering of offerings) {
        const {id, bundledProductOfferingOption} = offering;
        const type = offering?.['@type'];

        if (type === ATOMIC_TYPE) {
            const itemsForOffering = productOrderItems.filter(
                (item) => item.productOffering?.id === id
            );

            for (const item of itemsForOffering) {
                if (classifiedItemsSet.has(item.id)) continue;

                classifiedItemsSet.add(item.id);

                const {numberRelOfferLowerLimit: lowerLimit = 0, numberRelOfferUpperLimit: upperLimit = 0} =
                bundledProductOfferingOption || {};

                if (isRelatedToShipping(shippingItem, item)) {
                    mergeCharacteristics(item, shippingItem);
                }

                if (lowerLimit >= 1 && upperLimit >= 1) {
                    if (!mandatoryItems.includes(item)) mandatoryItems.push(item);
                } else if (lowerLimit === 0 && upperLimit >= 1) {
                    if (checkIfRelatedToShipping(shippingItem, item)) {
                        if (!devicesItems.includes(item)) devicesItems.push(item);
                    } else {
                        if (!optionalItems.includes(item)) optionalItems.push(item);
                    }
                }
            }
        } else if (type === BUNDLE_TYPE) {
            const nestedOfferings = await fetchBundledOfferingsByContractId(id);
            const nestedResults = await classifyItemsRecursively(
                nestedOfferings,
                productOrderItems,
                shippingItem,
                classifiedItemsSet
            );
            nestedResults.mandatoryItems.forEach(item => {
                if (!mandatoryItems.includes(item)) mandatoryItems.push(item);
            });
            nestedResults.optionalItems.forEach(item => {
                if (!optionalItems.includes(item)) optionalItems.push(item);
            });
            nestedResults.devicesItems.forEach(item => {
                if (!devicesItems.includes(item)) devicesItems.push(item);
            });
        }
    }

    return {mandatoryItems, optionalItems, devicesItems};
};

// Main function to fetch and process order details
export const fetchOrderDetails = async (productOrderId, planId, orderType, tNotification) => {
    const data = await fetchOrder(productOrderId, tNotification);
    const relatedParty = data.relatedParty?.[0];
    const productOrderItems = data.productOrderItem || [];

    const deletedItems = getDeletedAtomicItems(productOrderItems, orderType);
    const contractItem = findContractItem(productOrderItems, orderType);
    const bundledOfferings = await getBundledOfferings(orderType, contractItem, planId);
    const shippingItem = findShippingItem(productOrderItems);

    const {mandatoryItems, optionalItems, devicesItems} = await classifyItemsRecursively(
        bundledOfferings,
        productOrderItems,
        shippingItem
    );

    return {
        ...data,
        relatedParty,
        mandatoryItems: mandatoryItems,
        optionalItems: optionalItems,
        devicesItems: devicesItems,
        shippingItem,
        shippingPrice: calculateItemPricing(shippingItem?.itemPrice),
        deletedItems: deletedItems
    };
};

const getDeletedAtomicItems = (items, orderType) => {
    if (orderType === "Migration") {
        return items.filter(item =>
            item?.action === "delete" &&
            item?.productOffering?.["@type"] === ATOMIC_TYPE
        );
    }
    return [];
};

const findContractItem = (productOrderItems, orderType) => {
    const isContractType = (item) => item.productOffering?.["@type"] === CONTRACT_TYPE;

    if (orderType === "Migration") {
        return productOrderItems.find(item =>
            isContractType(item) &&
            hasValidMigrationRelationship(item)
        );
    }

    return productOrderItems.find(isContractType);
};

const hasValidMigrationRelationship = (item) => {
    const relationships = item?.productOrderItemRelationship;

    if (!relationships || !Array.isArray(relationships) || relationships.length === 0) {
        return false;
    }

    return relationships.some(
        relationship => relationship?.relationshipType === "migrateFrom"
    );
};

const getBundledOfferings = async (orderType, contractItem, planId) => {
    if (shouldFetchFromContract(orderType, contractItem)) {
        return await fetchBundledOfferingsByContractId(contractItem.productOffering.id);
    }

    if (orderType === 'Modification') {
        return await fetchBundledOfferingsForModification(planId);
    }

    return [];
};

const shouldFetchFromContract = (orderType, contractItem) => {
    return (orderType.startsWith('Acquisition') || orderType === 'Migration') && contractItem;
};

const fetchBundledOfferingsForModification = async (planId) => {
    const inventoryResponse = await apiClient.get(`${productInventoryURL}/${planId}`);
    const contractProductId = inventoryResponse.data.productOffering.id;
    return await fetchBundledOfferingsByContractId(contractProductId);
};

const findShippingItem = (productOrderItems) => {
    return productOrderItems.find(item =>
        item.product?.productSpecification?.["@baseType"] === SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE
    );
};