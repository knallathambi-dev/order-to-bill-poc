// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback} from "react";
import {toast} from "react-toastify";
import {performActionAndGetCharacteristics} from "../services/productConfigurationService";
import {useConfiguration} from "../context/ConfigurationContext";

export const useOptionChange = (setSelectedConfigItems) => {
    const {configuration, onConfigurationChange, relatedParty, dispatch, tNotification} = useConfiguration();

    return useCallback(
        async (configurationItemId, isSelected) => {
            const result = await performActionAndGetCharacteristics({
                configurationId: configuration.id,
                configurationItemId,
                characteristic: null,
                isSelected,
                relatedParty,
                actionType: 'add',
                dispatch,
                tNotification,
                onConfigurationChange,
            });

            if (!result) {
                toast.error(tNotification("plan.updateCharacteristicFailed"));
                return;
            }

            setSelectedConfigItems((prevState) => ({
                ...prevState,
                [configurationItemId]: {characteristics: result.updatedCharacteristics},
            }));
        },
        [configuration?.id, relatedParty, dispatch, tNotification, setSelectedConfigItems, onConfigurationChange]
    );
};