// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;


 class DescribeProductSpecCommandTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(DescribeProductSpecCommandTest.class);

    private final String brand = "CISCO";
    private final String productNumber = "123";
    private final String productDescription = "description_1";
    private final String productName = "name_1";
    private final EntityType type = EntityType.PRODUCTSPECIFICATION;
    private final String aggregateId = "1";

    @Test
    void describeProductSpecCommandTest() {
    	IdentityData desc=new IdentityData();
    	desc.setBrand(brand);
    	desc.setDescription(productDescription);
    	desc.setName(productName);
    	desc.setProductNumber(productNumber);
    	
        DefineIdentityProductSpecCommand describeProductSpecCommand = new DefineIdentityProductSpecCommand(aggregateId,desc
        		,new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod(),EntityType.PRODUCTSPECIFICATION);
        Assertions.assertAll("descriptionSpecProcessing",
                () -> assertEquals(describeProductSpecCommand.getDefineIdentityData().getBrand(), brand),
                () -> assertEquals(describeProductSpecCommand.getDefineIdentityData().getProductNumber(), productNumber),
                () -> assertEquals(describeProductSpecCommand.getDefineIdentityData().getDescription(), productDescription),
                () -> assertEquals(describeProductSpecCommand.getDefineIdentityData().getName(), productName),
                () -> assertEquals(describeProductSpecCommand.getType(), type));
        LOGGER.info("describeProductSpecCommand: {}", describeProductSpecCommand);
    }

}
