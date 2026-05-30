// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util.creator;

import com.fasterxml.jackson.databind.JsonNode;
import com.orange.discobole.productinventory.dto.v1.PatchOperationType;
import com.orange.discobole.productinventory.dto.v1.ProductPatch;
import lombok.SneakyThrows;

public class ProductPatchCreator {


    @SneakyThrows
    public static ProductPatch createProductPatchBuilder(String operation, String path, JsonNode value) {
        return ProductPatch.builder()
                .op(PatchOperationType.fromValue(operation))
                .path(path)
                .value(value)
                .build();


    }
    @SneakyThrows
    public static ProductPatch createProductPatchBuilderForRemove(String operation, String path) {
        return ProductPatch.builder()
                .op(PatchOperationType.fromValue(operation))
                .path(path)

                .build();


    }

}

