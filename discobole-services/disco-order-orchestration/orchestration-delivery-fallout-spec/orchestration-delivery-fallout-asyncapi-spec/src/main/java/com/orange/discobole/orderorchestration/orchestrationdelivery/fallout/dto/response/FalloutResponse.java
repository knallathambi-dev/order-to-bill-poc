// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.response;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;

import lombok.Data;

@Data
public class FalloutResponse {
    private HttpHeaders responseHeaders;
    private HttpStatus httpStatus;
    private List<FalloutIncident> falloutList;
}
