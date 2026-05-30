// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect, useState} from "react";
import {initializeRadioSelection, initializeState} from "../services/configurationUtils";

export const useConfigurationState = (configItems) => {
    const [selectedConfigItems, setSelectedConfigItems] = useState(() => initializeState(configItems));
    const [radioSelection, setRadioSelection] = useState(() => initializeRadioSelection(configItems));

    useEffect(() => {
        if (!Array.isArray(configItems)) return;

        const newSelectedItems = {...selectedConfigItems};
        const newRadioSelection = {...radioSelection};

        configItems.forEach((configItem) => {
            const {items} = configItem;
            items.forEach((item) => {
                const {id, productConfiguration} = item;
                if (!id || !productConfiguration) return;

                const configurationCharacteristics = (
                    productConfiguration.configurationCharacteristic || []
                ).filter(Boolean).filter((c) => c.isConfigurable);

                configurationCharacteristics.forEach((characteristic) => {
                    const {name} = characteristic;
                    const key = `${id}-${name}`;

                    if (!newSelectedItems[id]) {
                        newSelectedItems[id] = {
                            id,
                            name: productConfiguration.productOffering.name,
                            characteristics: {},
                        };
                    }

                    if (!newSelectedItems[id]?.characteristics[name]) {
                        const defaultValue =
                            characteristic.configurationCharacteristicValues[0]?.characteristic?.value || "";
                        newSelectedItems[id].characteristics[name] = {
                            id:
                                characteristic.configurationCharacteristicValues[0]?.characteristic?.id ||
                                "",
                            name,
                            value: defaultValue,
                            type:
                                characteristic.configurationCharacteristicValues[0]?.characteristic?.[
                                    "@type"
                                    ],
                        };
                    }

                    if (!newRadioSelection[key]) {
                        newRadioSelection[key] =
                            newSelectedItems[id].characteristics[name].value;
                    }
                });
            });
        });

        setSelectedConfigItems(newSelectedItems);
        setRadioSelection(newRadioSelection);
    }, [configItems]);

    return {selectedConfigItems, radioSelection, setSelectedConfigItems, setRadioSelection};
};