// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule;

import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyRuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class DiscoPolicyRuleServiceApplicationTests {
	@Mock
	private PolicyRuleServiceImpl policyRuleService;

	@Test
	void contextLoads() {
		assertNotNull(policyRuleService);
	}
}
