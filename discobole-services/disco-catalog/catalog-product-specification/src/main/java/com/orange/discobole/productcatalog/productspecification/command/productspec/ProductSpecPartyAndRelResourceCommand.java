// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;
import java.util.Map;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;

public final class ProductSpecPartyAndRelResourceCommand {

	private final List<RelatedParty> relatedParties;
	private final Map<String, String> realtedResources;

	public ProductSpecPartyAndRelResourceCommand(List<RelatedParty> relatedParties,
			Map<String, String> realtedResources) {
		this.relatedParties = relatedParties;
		this.realtedResources = realtedResources;
	}

	@Override
	public String toString() {
		return "ProductSpecPartyAndRelResourceCommand [relatedParties=" + relatedParties + ", realtedResources="
				+ realtedResources + "]";
	}

	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public Map<String, String> getRealtedResources() {
		return realtedResources;
	}

}
