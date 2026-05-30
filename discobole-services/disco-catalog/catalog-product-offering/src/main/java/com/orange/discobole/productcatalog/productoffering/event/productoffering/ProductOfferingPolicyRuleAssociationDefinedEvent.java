// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PolicyRuleAssociation;

/**
 * Event raised to show the all valid selected PolicyRuleAssociation from the user, that
 * can be defined.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
public class ProductOfferingPolicyRuleAssociationDefinedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<PolicyRuleRef> productOfferingPolicyRuleAssociation;
	private final OffsetDateTime lastUpdate;

	private ProductOfferingPolicyRuleAssociationDefinedEvent() {
		this.productOfferingId = null;
		this.productOfferingPolicyRuleAssociation = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPolicyRuleAssociationDefinedEvent(String productOfferingId,
			List<PolicyRuleRef> productOfferingPolicyRuleAssociation, OffsetDateTime lastUpdate
			) {

		this.productOfferingId = productOfferingId;
		this.productOfferingPolicyRuleAssociation = productOfferingPolicyRuleAssociation;
		this.lastUpdate = lastUpdate;
	}

	

	@Override
	public String toString() {
		return "ProductOfferingPolicyRuleAssociationDefinedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingPolicyRuleAssociation=" + productOfferingPolicyRuleAssociation + ", lastUpdate="
				+ lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<PolicyRuleRef> getProductOfferingPolicyRuleAssociation() {
		return productOfferingPolicyRuleAssociation;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
}
