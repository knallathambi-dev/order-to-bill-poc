// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.dto;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderResponse {
    private HttpHeaders responseHeaders;

    private HttpStatus httpStatus;

    private List<ProductOrder> productOrders;
}