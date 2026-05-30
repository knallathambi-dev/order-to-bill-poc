// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.orange.discobole.productinventory.model.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductEntityValidationHandler {

    private final List<ProductEntityValidator> validators;

    @Autowired
    public ProductEntityValidationHandler(List<ProductEntityValidator> validators) {
        this.validators = new ArrayList<>(validators);
    }

    public void validate(ProductEntity product) {
        for (ProductEntityValidator validator : validators) {
            validator.validate(product);
        }
    }
}
