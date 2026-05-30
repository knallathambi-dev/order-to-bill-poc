package com.orange.discobole.eventservice.controller;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Class HomeController.
 * 
 * @author Saurabh Shakya
 * @since 1.0
 * 
 */
@Controller
@RequestMapping("/")
public class HomeController {
	
	/**
	 * Endpoint for swagger gui.
	 *
	 * @return Swagger UI
	 */
	@GetMapping
	public ModelAndView swagger() {
		return new ModelAndView("redirect:/swagger-ui.html");
	}
	
}
