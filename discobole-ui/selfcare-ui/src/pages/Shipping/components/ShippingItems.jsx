// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import {useState} from "react";
import ShippingMethod from "./ShippingMethod";
import {useSelector} from "react-redux";
import useTranslations from "../../../utlis/i18n/useTranslations";

export function ShippingItems({onShippingFormValidation}) {
    const [selectedShop, setSelectedShop] = useState(1);
    const {itemsRequiringShipping = []} = useSelector((state) => state.itemsRequiringShipping);
    const numberOfItems = itemsRequiringShipping.length;
    const {t} = useTranslations();

    const verb = numberOfItems > 1 ? t("shipping.verbs.are") : t("shipping.verbs.is");

    return (
        <div className="card">
            <div className="card-body p-6">
                <h2 className="h5 mb-2">{t("shipping.shippingItems")}</h2>
                <p className="text-muted">
                    {t("shipping.offerShipping", {count: numberOfItems, verb: verb})}
                </p>
                <ShippingMethod
                    selectedShop={selectedShop}
                    setSelectedShop={setSelectedShop}
                    onShippingFormValidation={onShippingFormValidation}
                    itemsRequiringShipping={itemsRequiringShipping}
                />
            </div>
        </div>
    );
}

ShippingItems.propTypes = {
    onShippingFormValidation: PropTypes.func.isRequired
};

export default ShippingItems;