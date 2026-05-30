// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class acts as a command that gets triggered to cancel the {@code ProductSpecification}.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public final class ProductSpecCancelModificationCommand {
    
	@TargetAggregateIdentifier
    private final String productSpecId;

    /**
     * Constructs a command object that is supplied to {@code ProductSpecAggregate}
     * to cancel the incomplete {@code ProductSpecification}.
     *
     * @param productSpecId product Specification id
     */
    public ProductSpecCancelModificationCommand(String productSpecId) {
        this.productSpecId = productSpecId;
    }

    @Override
    public String toString() {
        return "ProductSpecCancelCommand{" +
                "productSpecId='" + productSpecId + '\'' +
                '}';
    }

    /**
     * This method is used to return the productSpecId associated with
     * {@link ProductSpecCancelModificationCommand}
     *
     * @return productSpecId the product spec id
     */
    public String getProductSpecId() {
        return productSpecId;
    }
}
