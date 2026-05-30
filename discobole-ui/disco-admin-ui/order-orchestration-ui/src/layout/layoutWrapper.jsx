// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {MfeLayoutWrapper} from "@discobole/common-ui";
import {OrchestrationDeliverySideMenuItems} from "../routes/OrchestrationDeliverySideMenuItems.jsx";
import {useAuth} from "../context/AuthContext";
import {env} from "../utils/env-helper.js";

export const LayoutWrapper = () => {
    const auth = useAuth();

    return (
        <MfeLayoutWrapper
            link="/orchestration-delivery/monitoring/orchestration-plans"
            sideMenuItems={OrchestrationDeliverySideMenuItems}
            standalone={env.STANDALONE_MODE}
            auth={auth}
        />
    );
};