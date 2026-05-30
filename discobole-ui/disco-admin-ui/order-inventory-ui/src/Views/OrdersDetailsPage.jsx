// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {LoadingIndicator, StatusPanel, useSideMenu} from "@discobole/common-ui";
import OrdersDetails from "../components/OrderDetails/OrdersDetails";
import api from "../service/OrderInventoryAPI.js";

function OrdersDetailsPage() {
    const {isActiveNav} = useSideMenu();
    const {id} = useParams();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchData = useCallback(async () => {
        if (!id) {
            setData(null);
            setLoading(false);
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await api.getProductOrder(id);
            setData(response);
        } catch (err) {
            setError(err);
            setData(null);
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return (
        <>
            {loading || error || !data ? (
                <>
                    {loading && <LoadingIndicator />}

                    {!loading && error && (
                        <StatusPanel
                            variant="error"
                            title="Something went wrong"
                            message={error?.message}
                            onAction={fetchData}
                            actionLabel="Retry"
                        />
                    )}

                    {!loading && !error && !data && (
                        <StatusPanel
                            variant="info"
                            title="No order found"
                            onAction={fetchData}
                            actionLabel="Refresh"
                        />
                    )}
                </>
            ) : (
                <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
                    <OrdersDetails data={data} />
                </div>
            )}
        </>
    );
}

export default OrdersDetailsPage;