// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import useTranslations from "../../../utlis/i18n/useTranslations";

export function ShippingAddressSummary({data, setEditInfo}) {
    const {t} = useTranslations();

    return (
        <div>
            <div className="fw-bold">{t("forms.labels.address")}</div>
            <p className="mt-1">
                {`${data.address}, ${data.city}, ${data.country} - ${data.postcode}`}
            </p>
            <div className="fw-bold">{t("forms.labels.requestedDeliveryDate")}</div>
            <p className="mt-1">{data.deliveryDate.split('-').reverse().join('/')}</p>
            <div onClick={() => {
                setEditInfo(true)
            }} className="fw-bold" style={{textDecoration: "underline", cursor: "pointer"}}>{t("shipping.edit")}
            </div>
        </div>
    )
}