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

import org.springframework.util.StringUtils;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingPriceLifecycle;

public class ProductOfferingPriceNextStates {
	
	private static final String RETIRED = "retired";
	
	public Set<String> getNextPossibleStates(String currentState, List<ProductOffering> productOfferings, List<ProductOfferingPrice> productOfferingPrices, String version) {

		Set<String> possibleStates = new HashSet<>();
		boolean flag = true;
		switch
		(currentState) {
		case "launched":
			possibleStates.add("unavailable");
				if(!productOfferingPrices.isEmpty())
				{
					flag=validateProductOfferingPriceStatus(productOfferingPrices);
				}
				
			if(flag && validateProductOfferingStatus(productOfferings) )
			{
				possibleStates.add(RETIRED);
			}
			
			break;
		case "unavailable":
			
			if(StringUtils.hasText(version))
			{
				possibleStates.add("launched");
			}
			if(!productOfferingPrices.isEmpty())
			{
				flag=validateProductOfferingPriceStatus(productOfferingPrices);
			}
			
		if(flag && validateProductOfferingStatus(productOfferings))
		{
			possibleStates.add(RETIRED);
		}

			
			break;
		case RETIRED:
			flag = checkForRetiredToObsolete(productOfferings);
			
				if(flag)
				{
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

	private boolean checkForRetiredToObsolete(List<ProductOffering> productOfferings) {
		for(ProductOffering productOffering:checkOfferings(productOfferings))
		{
			if(!ProductOfferingLifecycle.OBSOLETE.equals(productOffering.getLifecycleStatus()))
			{
				return false;
			}
		}
		return true;
	}

	private boolean validateProductOfferingStatus(List<ProductOffering> productOfferings)
	{
		for(ProductOffering productOffering:checkOfferings(productOfferings))
		{
			if(ProductOfferingLifecycle.ACTIVE.equals(productOffering.getLifecycleStatus())||ProductOfferingLifecycle.LAUNCHED.equals(productOffering.getLifecycleStatus()))
			{
				return false;
			}
		}
		
		return true;
		
	}
	
	private boolean validateProductOfferingPriceStatus(List<ProductOfferingPrice> productOfferingPrices)
	{
		for( ProductOfferingPrice productOfferingPrice:productOfferingPrices)
		{
			if(ProductOfferingPriceLifecycle.UNAVAILABLE.equals(productOfferingPrice.getLifecycleStatus())||ProductOfferingPriceLifecycle.LAUNCHED.equals(productOfferingPrice.getLifecycleStatus()))
			{
				return false;
			}
		}
		
		return true;
		
	}
	
	public static List<ProductOffering> checkOfferings(List<ProductOffering> productOfferings) {
		return productOfferings == null ? new ArrayList<>() : productOfferings;
	}

}
