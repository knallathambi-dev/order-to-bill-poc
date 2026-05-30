// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect} from "react";
import {useSelector} from "react-redux";
import useTranslations from "../../../../utlis/i18n/useTranslations";

function ShippingStore({shops, selectedShop, handleShopSelection}) {
    const {shippingType} = useSelector((state) => state.shipping);
    const {t} = useTranslations();

    useEffect(() => {
        if (shippingType === 2) {
            handleShopSelection(1);
        } else {
            handleShopSelection(0);
        }
    }, [shippingType, handleShopSelection]);

    return (
        <>
            <ul className="list-group  my-2">
                {shops.map((item) => {
                    return (
                        <li key={item.id} className="list-group-item">
                            <input
                                className="form-check-input me-1"
                                type="radio"
                                checked={selectedShop === item.id}
                                onChange={() => handleShopSelection(item.id)}
                                name="listGroupRadio"
                                id={`shopRadio-${item.id}`}
                            />
                            <label
                                className="form-check-label"
                                htmlFor={`shopRadio-${item.id}`}
                            >
                                {item.name}
                            </label>
                        </li>
                    );
                })}
            </ul>

            <p className="mt-1 mb-0 text-muted">
                <small className="fw-bold">
                    {t("shipping.pickUpMessage")}
                </small>
            </p>
        </>
    )
}

export default ShippingStore