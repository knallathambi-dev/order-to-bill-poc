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
 * This class acts as a command that links ProductSpecification To StockItem
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class AssociateProductSpecificationToStockItemCommand {
	@TargetAggregateIdentifier
    private final String prodSpecId;

    public AssociateProductSpecificationToStockItemCommand(String prodSpecId) {
        this.prodSpecId = prodSpecId;
    }

	@Override
	public String toString() {
		return "AssociateProductSpecificationToStockItemCommand{" + "prodSpecId='" + prodSpecId + '}';
	}

    public String getProdSpecId() {
        return prodSpecId;
    }
    
}
