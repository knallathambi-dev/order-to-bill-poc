// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;

import com.orange.discobole.productinventory.model.BaseDateDerivedFields;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
public class BaseDateDerivedFieldsCallback implements BeforeConvertCallback<BaseDateDerivedFields> {
    @Override
    public BaseDateDerivedFields onBeforeConvert(BaseDateDerivedFields entity, String collection) {
        entity.computeDerivedFields();
        return entity;
    }
}
