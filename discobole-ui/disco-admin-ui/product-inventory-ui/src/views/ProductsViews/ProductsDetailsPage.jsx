// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useRef, useState,} from "react";
import {useParams} from "react-router-dom";
import {LoadingIndicator, StatusPanel, useSideMenu,} from "@discobole/common-ui";

import ProductsDetails from "../../components/Products/ProductsDetails";
import api from "../../service/ProductInventoryAPI.js";

const getRootProductId = (product) => {
    if (!product) return null;

    const isNonContract =
        (product.productOffering?.["@type"] &&
            product.productOffering["@type"] !== "Contract") ||
        product.productSpecification;

    if (isNonContract) {
        const rootRel = product.productRelationship?.find(
            (rel) => rel.relationshipType === "rootProduct"
        );
        if (rootRel?.product?.id) return rootRel.product.id;
    }

    return product.id;
};

function ProductsDetailsPage() {
    const {isActiveNav} = useSideMenu();
    const {id} = useParams();
    const fetchRef = useRef(false);

    const [data, setData] = useState(null);
    const [productHierarchy, setProductHierarchy] = useState([]);
    const [parentContractHierarchy, setParentContractHierarchy] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchParentContractHierarchy = useCallback(async (parentId) => {
        try {
            const [parentRes, hierarchyRes] = await api.fetchProductHierarchy(parentId);
            setParentContractHierarchy([parentRes.data, ...hierarchyRes.data]);
        } catch (err) {
            console.error("Error fetching parent contract hierarchy:", err);
            setError(err);
        }
    }, []);

    const fetchMigratedProducts = useCallback(async (rootProduct) => {
        const orderItems = rootProduct?.productOrderItem;
        if (!orderItems?.length) return [];

        const migrateOrderIds = [
            ...new Set(
                orderItems
                    .filter((item) => item.orderItemAction === "migrate")
                    .map((item) => item.productOrderId)
            ),
        ];

        if (!migrateOrderIds.length) return [];

        try {
            return await api.getProductsByOrderIds(migrateOrderIds);
        } catch (err) {
            console.error("Error fetching migrated products:", err);
            return [];
        }
    }, []);

    const fetchData = useCallback(async () => {
        if (fetchRef.current) return;
        fetchRef.current = true;

        setLoading(true);
        setError(null);

        try {
            const requestedProduct = await api.getProductById(id);
            const rootProductId = getRootProductId(requestedProduct);

            const rootProduct =
                rootProductId && rootProductId !== requestedProduct.id
                    ? await api.getProductById(rootProductId)
                    : requestedProduct;

            const [migratedProducts, relatedProducts] = await Promise.all([
                fetchMigratedProducts(rootProduct),
                api.getProductsByRelationship(rootProduct.id),
            ]);

            setData(requestedProduct);
            setProductHierarchy([
                rootProduct,
                ...(relatedProducts || []),
                ...(migratedProducts || []),
            ]);
        } catch (err) {
            console.error("Error fetching data:", err);
            setError(err);
        } finally {
            setLoading(false);
            fetchRef.current = false;
        }
    }, [id, fetchMigratedProducts]);

    const refreshData = useCallback(() => {
        fetchRef.current = false;
        fetchData();
    }, [fetchData]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    if (loading) return <LoadingIndicator/>;

    if (error) {
        return (
            <StatusPanel
                variant="error"
                title="Something went wrong"
                message={error?.message}
                onAction={refreshData}
                actionLabel="Retry"
            />
        );
    }

    if (!data) {
        return (
            <StatusPanel
                variant="info"
                title="No product found"
                onAction={refreshData}
                actionLabel="Refresh"
            />
        );
    }

    return (
        <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
            <ProductsDetails
                dto={data}
                refreshData={refreshData}
                productHierarchy={productHierarchy}
                parentContractHierarchy={parentContractHierarchy}
                onProdParentContract={fetchParentContractHierarchy}
            />
        </div>
    );
}

export default ProductsDetailsPage;