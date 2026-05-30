// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Breadcrumb} from "../../components";
import "../CompleteOrder/CompleteOrder.css";
import React, {useState} from "react";
import {useTitlePage} from "../../hooks";
import OrderAppointmentSummary from "./components/OrderAppointmentSummary";
import OrderPreview from "./components/OrderPreview";
import useTranslations from "../../utlis/i18n/useTranslations";

function BookAppointment() {
    useTitlePage("bookAppointment");

    const [isOrderAppointmentValid, setIsOrderAppointmentValid] = useState(false);
    const {t} = useTranslations();

    return (
        <main>
            <div className="container">
                <Breadcrumb/>
                <div className="row">
                    <div className="col-md-12">
                        <h1 className="banner-title mb-4">{t("pages.bookAppointment")}</h1>
                    </div>
                </div>
                <div className="row">
                    <div className="col-12 col-md-7 col-lg-8">
                        <h2 className="mb-3">{t('appointment.title')}</h2>
                    </div>
                    <div className="col-12 col-lg-4 col-md-5">
                        <h4 className="mb-3">{t('common.orderPreview')}</h4>
                    </div>
                </div>
                <div className="row align-items-stretch">
                    <OrderAppointmentSummary setIsOrderAppointmentValid={setIsOrderAppointmentValid}/>
                    <OrderPreview isOrderAppointmentValid={isOrderAppointmentValid}/>
                </div>
            </div>
        </main>);
}

export default BookAppointment;