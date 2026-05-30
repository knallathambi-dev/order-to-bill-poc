// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;

public class InitiateProductSpecCommandTest extends ProductSpecificationApplicationTests {
	
	private static final Logger LOGGER = LogManager.getLogger(InitiateProductSpecCommandTest.class);

	private final String serviceSpecId = "701";

	@Test
	public void initiateProductSpecCommandTest() {
		InitiateProductSpecCommand initiateProductSpecCommand = new InitiateProductSpecCommand("1",serviceSpecId);
		Assertions.assertAll("initiateProductSpec", () -> assertEquals(initiateProductSpecCommand.getServiceSpecId(), serviceSpecId));
		LOGGER.info("initiateProductSpecCommand: {}", initiateProductSpecCommand);
	}

}
