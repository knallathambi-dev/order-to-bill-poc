// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FalloutCharacteristicWrapper {
    private CoodError coodError;
    private String topicName;
    private String traceParent;
    private String handlerClass;
    private Object eventPayload;
}
