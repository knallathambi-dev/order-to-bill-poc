// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useMemo} from "react";
import PropTypes from "prop-types";
import {StatusPanel} from "@discobole/common-ui";
import AccordionSection from "./AccordionSection";
import GroupedCharacteristics from "./GroupedCharacteristics.jsx";
import AddressCharacteristics from "./AddressCharacteristic.jsx";

const groupByType = (characteristics) =>
    (characteristics || []).reduce((acc, char) => {
        const type = char["@type"];
        if (!acc[type]) acc[type] = [];
        acc[type].push(char);
        return acc;
    }, {});

const ProductCharacteristicDetails = ({sections, onClick, productCharacteristic}) => {
    const groupedByType = useMemo(() => groupByType(productCharacteristic), [productCharacteristic]);

    const addressCharacteristics = groupedByType["AddressCharacteristic"] || [];
    const otherCharacteristics = useMemo(() => {
        const {AddressCharacteristic, ...rest} = groupedByType;
        return rest;
    }, [groupedByType]);
    const hasOtherCharacteristics = Object.keys(otherCharacteristics).length > 0;
    const hasNoData = !productCharacteristic || productCharacteristic.length === 0;

    return (
        <AccordionSection
            title="Product Characteristics"
            isExpanded={sections[2].isExpanded}
            onToggle={onClick}
        >
            {addressCharacteristics.length > 0 && (
                <AddressCharacteristics addresses={addressCharacteristics}/>
            )}

            {hasOtherCharacteristics && (
                <GroupedCharacteristics groupedCharacteristics={otherCharacteristics}/>
            )}

            {hasNoData && (
                <StatusPanel
                    variant="info"
                    title="No Characteristics Available"
                    message="There are no product characteristics to display."
                />
            )}
        </AccordionSection>
    );
};

ProductCharacteristicDetails.propTypes = {
    sections: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
    productCharacteristic: PropTypes.array,
};

export default ProductCharacteristicDetails;