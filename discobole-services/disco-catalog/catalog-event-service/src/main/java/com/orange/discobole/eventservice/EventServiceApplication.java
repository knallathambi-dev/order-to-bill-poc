package com.orange.discobole.eventservice;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class EventServiceApplication to start the application.
 * 
 * @author Saurabh Shakya
 * @since 1.0
 */
//@EnableEurekaClient
@SpringBootApplication
public class EventServiceApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}

}
