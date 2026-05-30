// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query;

import java.util.ArrayList;
import java.util.List;

public class CompositeValidator implements QueryValidator {

    private final List<QueryValidator> validators;

    public CompositeValidator(List<QueryValidator> validators) {
        // Defensive copy to avoid exposing the mutable list
        this.validators = new ArrayList<>(validators);
    }

    public static CompositeValidatorBuilder builder() {
        return new CompositeValidatorBuilder();
    }

    @Override
    public void validate(String query) {
        for (QueryValidator validator : validators) {
            validator.validate(query);
        }
    }

    public static class CompositeValidatorBuilder {

        private final List<QueryValidator> validators = new ArrayList<>();

        public CompositeValidatorBuilder addValidator(QueryValidator validator) {
            validators.add(validator);
            return this;
        }

        public CompositeValidator build() {
            return new CompositeValidator(validators);
        }
    }

}