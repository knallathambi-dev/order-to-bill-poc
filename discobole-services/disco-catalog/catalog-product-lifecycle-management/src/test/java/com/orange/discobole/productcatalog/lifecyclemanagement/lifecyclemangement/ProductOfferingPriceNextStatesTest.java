// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemangement;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.ProductOfferingPriceNextStates;


/**
 * @author BMKJ8547
 *
 */

class ProductOfferingPriceNextStatesTest {

private ProductOfferingPriceNextStates productOfferingPriceNextStates;


@BeforeEach
void setUp() {
	productOfferingPriceNextStates = new ProductOfferingPriceNextStates();
}

/**
 * To get the next possible states for POPA when current state is Launched.
 * linked ProductOffering is in Launched State .
 * linked POPC is in Launched State .
 * 
 * 
 */
@Test

void getNextPossibleStatesForProducrOfferingPriceCase1()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("launched",productOfferings,productOfferingPrices,"version");
	 Assert.assertEquals(true,possibleStates.contains("unavailable"));
}

/**
 * To get the next possible states for POPA when current state is Launched.
 * linked ProductOffering is in Launched State .
 */
@Test
void getNextPossibleStatesForProducrOfferingPriceChargeCase1()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("launched",productOfferings,new ArrayList<ProductOfferingPrice>(),"version");
	 Assert.assertEquals(true,possibleStates.contains("unavailable"));
}


 /**
 * To get the next possible states for POPA when current state is Launched.
 * linked ProductOffering is in Retired State .
 * linked POPC is in Retired State .
 */
 
@Test
void getNextPossibleStatesForProductOfferingPriceCase2()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.RETIRED));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("launched",productOfferings,productOfferingPrices,"version");
	 Assert.assertEquals(true,possibleStates.contains("unavailable"));
	 Assert.assertEquals(true,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPC when current state is Launched.
 * linked ProductOffering is in Retired State .
 */
@Test
void getNextPossibleStatesForProductOfferingPriceChargeCase2()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("launched",productOfferings,new ArrayList<ProductOfferingPrice>(),"version");
	 Assert.assertEquals(true,possibleStates.contains("unavailable"));
	 Assert.assertEquals(true,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPC when current state is Unavailable.
 * linked ProductOffering is in Active State .
 */
@Test
void getNextPossibleStatesForProductOfferingPriceChargrCase3()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,new ArrayList<ProductOfferingPrice>(),"version");
	 Assert.assertEquals(true,possibleStates.contains("launched"));
	 Assert.assertEquals(false,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPC when current state is Unavailable.
 * linked ProductOffering is in Active State .
 * POPC is not validated.
 * 
 */
@Test
 void getNextPossibleStatesForProductOfferingPriceChargeCase4()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,new ArrayList<ProductOfferingPrice>(),null);
	 Assert.assertEquals(false,possibleStates.contains("launched"));
	 Assert.assertEquals(false,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPC when current state is Unavailable.
 * linked ProductOffering is in Retired State .
 */
@Test
void getNextPossibleStatesForProductOfferingPriceChargeCase5()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));

	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,new ArrayList<ProductOfferingPrice>(),"version");
	 Assert.assertEquals(true,possibleStates.contains("launched"));
	 Assert.assertEquals(true,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPA when current state is Unavailable.
 * linked ProductOffering is in Active State .
 * linked POPC is Retired.
 */
@Test
void getNextPossibleStatesForProductOfferingPriceCase3()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.RETIRED));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,productOfferingPrices,"version");
	 Assert.assertEquals(true,possibleStates.contains("launched"));
	 Assert.assertEquals(false,possibleStates.contains("retired")); 
}
/**
 * To get the next possible states for POPA when current state is Unavailable.
 * linked ProductOffering is in Retired State .
 * linked POPC is Retired.
 */
@Test
void getNextPossibleStatesForProductOfferingPriceCase4()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.RETIRED));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,productOfferingPrices,"version");
	 Assert.assertEquals(true,possibleStates.contains("launched"));
	 Assert.assertEquals(true,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPA when current state is Unavailable.
 * linked ProductOffering is in Retired State .
 * linked POPC is Retired.
 * POPA is not validated.
 */
@Test
void getNextPossibleStatesForProductOfferingPriceCase5(){
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.RETIRED));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("unavailable",productOfferings,productOfferingPrices,null);
	 Assert.assertEquals(false,possibleStates.contains("launched"));
	 Assert.assertEquals(true,possibleStates.contains("retired"));
}

/**
 * To get the next possible states for POPC when current state is Retired.
 * linked ProductOffering is in Retired State .
 */
@Test
void getNextPossibleStatesForProductOfferingPriceChargeCase6()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.RETIRED));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("retired",productOfferings,productOfferingPrices,null);
	 Assert.assertEquals(false,possibleStates.contains("obsolete"));
}

/**
 * To get the next possible states for POPC when current state is Retired.
 * linked ProductOffering is in Obsolete State .
 */
@Test
void getNextPossibleStatesForProductOfferingPriceChargeCase7()
{
	List<ProductOffering> productOfferings = new ArrayList<>();
	productOfferings.add(new ProductOffering().id("prodOff1").lifecycleStatus(ProductOfferingLifecycle.OBSOLETE));
	List<ProductOfferingPrice> productOfferingPrices = new ArrayList<>();
	productOfferingPrices.add(new ProductOfferingPrice().id("POP1").lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE));
	 Set<String> possibleStates=productOfferingPriceNextStates.getNextPossibleStates("retired",productOfferings,productOfferingPrices,null);
	 Assert.assertEquals(true,possibleStates.contains("obsolete"));
}


}
