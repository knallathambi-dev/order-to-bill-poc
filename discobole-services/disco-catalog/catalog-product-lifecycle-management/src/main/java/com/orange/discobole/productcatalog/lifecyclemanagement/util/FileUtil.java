// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileUtil reads a file from a particular path.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
	}

	/**
	 * Read method reads a file from filepath.
	 *
	 * @param filePath the file path
	 * @return the string
	 */
	public static String read(final String filePath) {
		final InputStream inputStream = FileUtil.class.getResourceAsStream(filePath);
		final StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append(' ');
			}
		} catch (final IOException e) {
			LOGGER.error("read(error) -> file: {}. Reason: {}", filePath, e.getMessage());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("read -> file: {}", filePath);
		}
		return resultStringBuilder.toString();
	}

}
