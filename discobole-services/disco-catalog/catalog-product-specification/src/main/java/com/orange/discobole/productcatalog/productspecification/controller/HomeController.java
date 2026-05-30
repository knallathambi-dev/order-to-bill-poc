// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * HomeController is used for swagger-ui
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Controller
@RequestMapping("/")
public class HomeController {

	@GetMapping
	public ModelAndView swagger() {
		return new ModelAndView("redirect:/swagger-ui.html");
	}

}
