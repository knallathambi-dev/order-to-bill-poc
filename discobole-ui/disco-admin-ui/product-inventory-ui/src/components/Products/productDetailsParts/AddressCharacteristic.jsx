// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";
import DataTable from "./DataTable";

const renderAddressLine = (char) => {
    const parts = [];
    const streetPart = [char.subUnitNumber, char.streetName].filter(Boolean).join(" ");
    if (streetPart) parts.push(streetPart);
    if (char.city) parts.push(char.city);
    if (char.country) parts.push(char.country);
    const postcodePart = char.postcode ? ` - ${char.postcode}` : "";
    return parts.join(", ") + postcodePart;
};

const AddressCharacteristic = ({addresses}) => (
    <div className="mb-3">
        <DataTable
            columns={["Type", "Address ID", "Name", "Address"]}
            data={addresses}
            rowKey={(char, index) => `${char.addressId || "no-id"}-${index}`}
            renderRow={(char) => [
                char["@type"],
                char.addressId,
                char.name,
                renderAddressLine(char) || "_",
            ]}
        />
    </div>
);

AddressCharacteristic.propTypes = {
    addresses: PropTypes.arrayOf(PropTypes.shape({
        "@type": PropTypes.string,
        addressId: PropTypes.string,
        name: PropTypes.string,
        subUnitNumber: PropTypes.string,
        streetName: PropTypes.string,
        city: PropTypes.string,
        country: PropTypes.string,
        postcode: PropTypes.string,
    })).isRequired,
};

export default AddressCharacteristic;