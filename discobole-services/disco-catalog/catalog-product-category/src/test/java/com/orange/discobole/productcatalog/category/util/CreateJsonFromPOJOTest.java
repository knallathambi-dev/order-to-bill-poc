// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.util;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.category.util.CreateJsonFromPOJO;

class CreateJsonFromPOJOTest {

	@InjectMocks
	private CreateJsonFromPOJO createJsonFromPOJO;

	@Test
	void test() throws IOException {
		String[] args = new String[0];
		CreateJsonFromPOJO.main(args);
		Assertions.assertNotNull(args);
	}
}
