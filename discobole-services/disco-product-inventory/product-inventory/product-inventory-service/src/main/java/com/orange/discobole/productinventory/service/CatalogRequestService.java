// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.CatalogEntityRef;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Set;


public interface CatalogRequestService {
    Mono<ResponseEntity<CatalogEntityRef[]>> listProductOfferings(Set<String> ids);
    Mono<ResponseEntity<CatalogEntityRef[]>> listProductSpecifications(Set<String> ids);
    Mono<ResponseEntity<CatalogEntityRef[]>> listProductOfferingPrices(Set<String> ids);
}