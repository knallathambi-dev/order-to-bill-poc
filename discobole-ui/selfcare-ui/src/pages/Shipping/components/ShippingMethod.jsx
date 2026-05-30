// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import {ShippingTabs} from "./ShippingTabs";
import ShippingForm from "./ShippingMethods/ShippingForm";
import ShippingStore from "./ShippingMethods/ShippingStore";
import {useState} from "react";
import useTranslations from "../../../utlis/i18n/useTranslations";

function ShippingMethod({selectedShop, setSelectedShop, onShippingFormValidation, itemsRequiringShipping}) {
    const [shippingSummary, setShippingSummary] = useState(false);
    const {t} = useTranslations();

    const handleShopSelection = (shop) => {
        if (shop === 1) {
            setSelectedShop(shop);
            onShippingFormValidation(true);
        } else if (shop === 0 && !shippingSummary) {
            onShippingFormValidation(false);
        }
    };

    const shops = [{
        id: 1,
        name: "Orange Canebière Shop"
    }];

    const shippingMethods = [
        {
            id: 1,
            name: t("forms.labels.shippingAddress"),
            component:
                <ShippingForm
                    onShippingFormValidation={onShippingFormValidation}
                    shippingSummary={shippingSummary}
                    setShippingSummary={setShippingSummary}
                />
        },
        {
            id: 2,
            name: t("forms.labels.pickUpFromStore"),
            component: <ShippingStore
                shops={shops}
                selectedShop={selectedShop}
                handleShopSelection={handleShopSelection}
            />
        }
    ];

    const sortedItemsRequiringShipping = [...itemsRequiringShipping].sort((a, b) =>
        a.productConfiguration.productOffering.name.localeCompare(b.productConfiguration.productOffering.name)
    );

    return (
        <div className="card mb-2">
            <div className="card-header">
                <div className="d-flex justify-content-between align-items-center">
                    <p className="me-auto mb-0">
                        <strong>{t("common.shipping")}</strong>
                    </p>
                </div>
            </div>
            <ul className="list-group list-group-flush">
                {sortedItemsRequiringShipping.map((item) => (
                    <li key={item.id} className="list-group-item">
                        {item.productConfiguration.productOffering.name}
                    </li>
                ))}
                <li className="list-group-item">
                    <ShippingTabs shippingMethods={shippingMethods}/>
                </li>
            </ul>
        </div>
    );
}

ShippingMethod.propTypes = {
    selectedShop: PropTypes.number.isRequired,
    setSelectedShop: PropTypes.func.isRequired,
    onShippingFormValidation: PropTypes.func.isRequired,
    itemsRequiringShipping: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.string.isRequired,
            productConfiguration: PropTypes.shape({
                productOffering: PropTypes.shape({
                    name: PropTypes.string.isRequired,
                }).isRequired,
            }).isRequired,
        }).isRequired
    ).isRequired
};

export default ShippingMethod;