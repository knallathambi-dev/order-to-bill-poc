// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const initializeState = (configItems) => {
    const initialSelectedIncluded = {};
    configItems.forEach((configItem) => {
        const {items} = configItem;
        items.forEach((item) => {
            const {productConfiguration, id} = item;
            if (!id || !productConfiguration) return;

            const configurationCharacteristics = (
                productConfiguration.configurationCharacteristic || []
            ).filter(Boolean);

            const selectedCharacteristics = configurationCharacteristics
                .filter((char) => char.isConfigurable)
                .reduce((acc, char) => {
                    const defaultChar =
                        char.configurationCharacteristicValues.find((val) => val.isSelected) ||
                        char.configurationCharacteristicValues[0];
                    if (defaultChar) {
                        acc[char.name] = {
                            id: defaultChar.characteristic.id,
                            name: defaultChar.characteristic.name,
                            value: defaultChar.characteristic.value,
                            type: defaultChar.characteristic["@type"],
                        };
                    }
                    return acc;
                }, {});

            initialSelectedIncluded[id] = {
                id,
                name: productConfiguration.productOffering?.name,
                characteristics: selectedCharacteristics,
            };
        });
    });
    return initialSelectedIncluded;
};

export const initializeRadioSelection = (configItems) => {
    const initialRadioSelection = {};
    configItems.forEach((configItem) => {
        const {items} = configItem;
        items.forEach((item) => {
            const {productConfiguration, id} = item;
            const configurationCharacteristics = (
                productConfiguration.configurationCharacteristic || []
            ).filter((c) => c !== null && c !== undefined);

            const selectedForInitial = configurationCharacteristics
                .filter(
                    (char) => char.isConfigurable && char.configurationCharacteristicValues.length > 1
                )
                .reduce((acc, char) => {
                    const defaultChar = char.configurationCharacteristicValues.find(
                        (val) => val.isSelected
                    );
                    if (defaultChar) {
                        acc[char.name] = {
                            id: defaultChar.characteristic.id,
                            name: defaultChar.characteristic.name,
                            value: defaultChar.characteristic.value,
                            type: defaultChar.characteristic["@type"],
                        };
                    }
                    return acc;
                }, {});

            Object.keys(selectedForInitial).forEach((name) => {
                initialRadioSelection[`${id}-${name}`] = selectedForInitial[name]?.value;
            });
        });
    });
    return initialRadioSelection;
};