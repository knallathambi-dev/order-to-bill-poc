// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;

public class ProductSpecNextSates {

	private static final String RETIRED = "retired";

	public Set<String> getNextPossibleStates(String currentState, List<ProductOffering> productOfferings) {

		Set<String> possibleStates = new HashSet<>();
		boolean flag = true;
		switch (currentState) {
		case "inTest":
			possibleStates.add("rejected");
			possibleStates.add("active");
			break;
		case "active":
			possibleStates.add("launched");
			flag = chekForRetired(productOfferings);

			if (flag) {
				possibleStates.add(RETIRED);
			}

			break;
		case "launched":
			flag = chekForRetired(productOfferings);

			if (flag) {
				possibleStates.add(RETIRED);
			} else {
				possibleStates.add("unavailable");
			}

			break;
		case "unavailable":
			flag = chekForRetired(productOfferings);

			if (flag) {
				possibleStates.add(RETIRED);
			}
			break;
		case RETIRED:
			flag = chekForRetiredToObsolete(productOfferings);

			if (flag) {
				possibleStates.add("obsolete");
			}
			break;
		case "rejected":
			break;
		default:
			break;
		}
		return possibleStates;
	}

	private boolean chekForRetired(List<ProductOffering> productOfferings) {
		for (ProductOffering productOffering : checkOfferings(productOfferings)) {
			ProductOfferingLifecycle status = productOffering.getLifecycleStatus();
			if (!status.equals(ProductOfferingLifecycle.RETIRED) && !status.equals(ProductOfferingLifecycle.OBSOLETE)) {
				return false;
			}
		}
		return true;
	}

	private boolean chekForRetiredToObsolete(List<ProductOffering> productOfferings) {
		for (ProductOffering productOffering : checkOfferings(productOfferings)) {
			ProductOfferingLifecycle status = productOffering.getLifecycleStatus();
			if (!status.equals(ProductOfferingLifecycle.OBSOLETE)) {
				return false;
			}
		}
		return true;
	}

	public static List<ProductOffering> checkOfferings(List<ProductOffering> productOfferings) {
		return productOfferings == null ? new ArrayList<>() : productOfferings;
	}

}
