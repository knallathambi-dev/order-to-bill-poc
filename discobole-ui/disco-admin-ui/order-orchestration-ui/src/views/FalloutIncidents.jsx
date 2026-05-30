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
import FalloutTabContent from "../components/Administration/FalloutTabContent";

const FalloutIncidents = () => {
    const reloadRef = useRef(null);
    const [loading, setLoading] = useState(false);

    return (
        <MonitoringPage
            title="Administration"
            actions={<ReloadButton onClick={() => reloadRef.current?.()} loading={loading}/>}
        >
            <FalloutTabContent reloadRef={reloadRef} setReloadLoading={setLoading}/>
        </MonitoringPage>
    );
};

export default FalloutIncidents;