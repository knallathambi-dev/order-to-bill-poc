// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;



class HomeControllerTest {

	@Test
	void testHomeControllerSwagger() {
		HomeController homeController = new HomeController();
		ModelAndView modelAndView = homeController.swagger();
		String expectedViewName = "redirect:/swagger-ui.html";
		assertEquals(expectedViewName, modelAndView.getViewName());
	}

	@Test
	void testHomeControllerUI() {
		HomeController homeController = new HomeController();
		ModelAndView modelAndView = homeController.ui();
		String expectedViewName = "redirect:/index.html";
		assertEquals(expectedViewName, modelAndView.getViewName());
	}
}