// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useRef, useState} from "react";
import {MonitoringPage, ReloadButton} from "@discobole/common-ui";
import OrchestrationTabContent from "../components/OrchestrationTabContent";

function OrchestrationMonitoringArchivedPlans() {
    const reloadRef = useRef(null);
    const [loading, setLoading] = useState(false);

    return (
        <MonitoringPage
            title="Archived Orchestration Plans"
            actions={<ReloadButton onClick={() => reloadRef.current?.()} loading={loading} tooltipPlace="left"/>}
        >
            <OrchestrationTabContent
                param={{key: "archived", value: true}}
                reloadRef={reloadRef}
                setReloadLoading={setLoading}
            />
        </MonitoringPage>
    );
}

export default OrchestrationMonitoringArchivedPlans;