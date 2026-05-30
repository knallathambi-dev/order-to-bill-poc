// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Link} from "react-router-dom";
import {useEffect, useMemo, useState} from "react";
import {useSelector} from "react-redux";
import {toast} from "react-toastify";
import {Modal} from "../../../components";
import CancelOrder from "../../OrderSummary/components/CancelOrder";
import {ADDRESS_CHARACTERISTIC_TYPE} from "../../../utlis/constants";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {fetchOrder} from "../../../services";

function OrderAppointmentSummary({setIsOrderAppointmentValid}) {
    const [order, setOrder] = useState(null);
    const [appointmentDate, setAppointmentDate] = useState("");
    const [isModalVisible, setIsModalVisible] = useState(false);

    const {productOrderId} = useSelector((state) => state.order);
    const {t, tNotification} = useTranslations();

    const minDate = useMemo(() => {
        return new Date().toISOString().split('T')[0];
    }, []);

    const addressCharacteristic = useMemo(() => {
        if (!order?.productOrderItem) return null;

        const char = order.productOrderItem
            .flatMap(item => item.product.productCharacteristic || [])
            .find(c => c['@type'] === ADDRESS_CHARACTERISTIC_TYPE);

        if (!char) return null;

        const parts = [];
        const streetPart = [char.subUnitNumber, char.streetName].filter(Boolean).join(' ');

        if (streetPart) parts.push(streetPart);
        if (char.city) parts.push(char.city);
        if (char.country) parts.push(char.country);

        const addressLine = parts.join(', ');
        return char.postcode ? `${addressLine} - ${char.postcode}` : addressLine;
    }, [order]);

    useEffect(() => {
        const fetchAndSetOrder = async () => {
            if (!productOrderId || order?.id === productOrderId) return;

            try {
                const fetchedOrder = await fetchOrder(productOrderId, tNotification);
                setOrder(fetchedOrder);
            } catch (error) {
                toast.error(tNotification("common.fetchOrderFailed"));
            }
        };

        fetchAndSetOrder();
    }, [productOrderId, order?.id]);

    useEffect(() => {
        setIsOrderAppointmentValid(!!appointmentDate);
    }, [appointmentDate, setIsOrderAppointmentValid]);

    const handleKeyDown = (e) => {
        if (e.key !== 'Tab' && e.key !== 'Escape') {
            e.preventDefault();
        }
    };

    return (
        <div className="col-12 col-md-7 col-lg-8 d-flex flex-column">
            <div className="card">
                <div className="card-body p-6">
                    <h2 className="h5 mb-2">{t('appointment.title')}</h2>
                    <p className="text-muted">{t('appointment.description')}</p>

                    <div className="appointment-details mb-3">
                        <div className="card">
                            <div className="card-body p-2">
                                <p className="mb-0">
                                    <strong>{t('forms.labels.address')}:</strong> {addressCharacteristic || 'N/A'}
                                </p>
                            </div>
                        </div>

                        <div className="mt-3">
                            <label htmlFor="appointmentDate" className="form-label">
                                {t('forms.labels.appointmentDate')}
                            </label>
                            <input
                                type="date"
                                className="form-control"
                                id="appointmentDate"
                                value={appointmentDate}
                                min={minDate}
                                onChange={(e) => setAppointmentDate(e.target.value)}
                                onKeyDown={handleKeyDown}
                            />
                        </div>
                    </div>
                </div>
            </div>
            <div className="mt-auto">
                <hr className="mt-4"/>
                <Link to="#" className="btn btn-outline-secondary mt-2 mb-5" onClick={() => setIsModalVisible(true)}>
                    {t("actions.cancelOrder")}
                </Link>
                {isModalVisible && (
                    <Modal
                        show={isModalVisible}
                        title={t("order.confirmations.cancel.title")}
                        body={<CancelOrder hideModal={() => setIsModalVisible(false)}/>}
                        onClose={() => setIsModalVisible(false)}
                        hideFooter
                        className="CancelOrderModal"
                    />
                )}
            </div>
        </div>
    );
}

export default OrderAppointmentSummary;