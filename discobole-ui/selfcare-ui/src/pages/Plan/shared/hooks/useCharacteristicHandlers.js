// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback} from "react";
import {performActionAndGetCharacteristics} from "../services/productConfigurationService";
import {useConfiguration} from "../context/ConfigurationContext";
import {toast} from "react-toastify";

export const useCharacteristicHandlers = (
    selectedConfigItems,
    setSelectedConfigItems,
    radioSelection,
    setRadioSelection,
) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, tNotification} = useConfiguration();

    const handleRangeChange = (e, configurationItemId, sortedValues, name) => {
        const normalizedValue = parseFloat(e.target.value);
        const actualValue =
            sortedValues[Math.round((normalizedValue / 100) * (sortedValues.length - 1))]
                .characteristic.value;
        setRadioSelection((prevState) => ({
            ...prevState,
            [`${configurationItemId}-${name}`]: actualValue,
        }));
    };

    const handleCharacteristicUpdate = useCallback(
        async (configurationItemId, characteristicData, newSelectedValue, actionType) => {
            const updatedCharacteristic = {
                id: characteristicData?.id,
                value: newSelectedValue,
                type: characteristicData?.['@type'],
            };

            const result = await performActionAndGetCharacteristics({
                configurationId: configuration.id,
                configurationItemId,
                characteristic: updatedCharacteristic,
                isSelected: true,
                relatedParty,
                actionType,
                dispatch,
                tNotification,
                onConfigurationChange,
            });

            if (!result) {
                toast.error(tNotification("plan.updateCharacteristicFailed"));
                return;
            }

            setSelectedConfigItems((prevConfigItems) => ({
                ...prevConfigItems,
                [configurationItemId]: {
                    ...prevConfigItems[configurationItemId],
                    characteristics: {
                        ...prevConfigItems[configurationItemId]?.characteristics,
                        ...result.updatedCharacteristics,
                    },
                },
            }));

            setRadioSelection((prevSelections) => ({
                ...prevSelections,
                [`${configurationItemId}-${characteristicData?.name}`]: newSelectedValue,
            }));
        },
        [configuration?.id, relatedParty, setSelectedConfigItems, setRadioSelection, dispatch, tNotification, onConfigurationChange]
    );

    const handleRangeChangeEnd = async (
        configurationItemId,
        sortedValues,
        characteristicData,
        actionType
    ) => {
        const {name: characteristicName} = characteristicData || {};
        const storedValue = radioSelection[`${configurationItemId}-${characteristicName}`];

        await handleCharacteristicUpdate(
            configurationItemId,
            characteristicData,
            storedValue,
            actionType
        );
    };

    return {handleRangeChange, handleRangeChangeEnd, handleCharacteristicUpdate};
};