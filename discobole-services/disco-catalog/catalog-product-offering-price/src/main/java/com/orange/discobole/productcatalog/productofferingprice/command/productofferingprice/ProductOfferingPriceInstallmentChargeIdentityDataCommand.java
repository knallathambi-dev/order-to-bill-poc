// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineInstallmentChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

/**
 * This class ProductOfferingPriceAlterationIdentityDataCommand is a command
 * which gathers input about the rprice altered value for product offering price
 * from the user.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceInstallmentChargeIdentityDataCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final DefineInstallmentChargeIdentityData identityData;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;

	public ProductOfferingPriceInstallmentChargeIdentityDataCommand(String productOfferingPriceId,
                                                                    DefineInstallmentChargeIdentityData identityData,
                                                                    DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod, List<ProductOfferingPriceRelationship> productOfferingPriceRelationships) {
		super();
		this.productOfferingPriceId = productOfferingPriceId;
		this.identityData = identityData;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
        this.productOfferingPriceRelationships = productOfferingPriceRelationships;
    }

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public DefineInstallmentChargeIdentityData getIdentityData() {
		return identityData;
	}

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceInstallmentChargeIdentityDataCommand{" +
				"productOfferingPriceId='" + productOfferingPriceId + '\'' +
				", identityData=" + identityData +
				", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod +
				", productOfferingPriceRelationships=" + productOfferingPriceRelationships +
				'}';
	}
}
