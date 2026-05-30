// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;

public class PublisherConfigTest extends CategoryApplicationTests {

@InjectMocks
PublisherConfig config;

@Test
public void testPublisher() {
	Publisher pub=config.publisher();
	assertNotNull(pub);
}
}
