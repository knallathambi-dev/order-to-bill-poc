// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Breadcrumb} from "../../components";
import "./CompleteOrder.css";
import OrderCompletionSummary from "./components/OrderCompletionSummary";
import OrderPreview from "./components/OrderPreview";
import React, {useState} from "react";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";

function CompleteOrder() {
    useTitlePage("completeOrder");

    const [isOrderCompletionValid, setOrderCompletionStatus] = useState(false);
    const [order, setOrder] = useState(null);
    const {t} = useTranslations();

    return (
        <main>
            <div className="container">
                <Breadcrumb/>
                <div className="row">
                    <div className="col-md-12">
                        <h1 className="banner-title mb-4">{t("pages.completeOrder")}</h1>
                    </div>
                </div>
                <div className="row">
                    <OrderCompletionSummary
                        setOrderCompletionStatus={setOrderCompletionStatus}
                        setOrder={setOrder}
                    />
                    <OrderPreview
                        isOrderCompletionValid={isOrderCompletionValid}
                        order={order}
                    />
                </div>
            </div>
        </main>
    );
}

export default CompleteOrder;