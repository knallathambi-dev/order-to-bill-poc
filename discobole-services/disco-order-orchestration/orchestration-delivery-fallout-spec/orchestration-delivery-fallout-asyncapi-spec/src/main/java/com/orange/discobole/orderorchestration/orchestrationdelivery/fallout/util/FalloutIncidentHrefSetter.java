// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.api.v1.FalloutIncidentApi;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class FalloutIncidentHrefSetter {

    private FalloutIncidentHrefSetter() {
    }

    public static String generateHref(String id, String fields) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FalloutIncidentApi.class)
                        .getFalloutById(id, fields)).toUriComponentsBuilder()
                .build().toUriString();
    }
}
