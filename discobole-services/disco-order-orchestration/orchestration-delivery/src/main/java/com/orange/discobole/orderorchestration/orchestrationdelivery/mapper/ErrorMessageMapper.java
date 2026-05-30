// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanErrorMessage;
import org.mapstruct.Mapper;

@Mapper
public interface ErrorMessageMapper {
    OrchestrationPlanErrorMessage mapToPlanError(CoodError coodError);

    OrchestrationNodeErrorMessage mapToNodeError(CoodError coodError);

    CoodError mapToCoodError(OrchestrationNodeErrorMessage orchestrationNodeErrorMessage);
}
