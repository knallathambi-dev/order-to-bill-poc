// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class FileUtil {

    private FileUtil() {
    }

    /**
     * Read method reads a file from filepath.
     *
     * @param filePath the file path
     * @return the string
     */
    public static String read(final String filePath) {
        final StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(FileUtil.class.getResourceAsStream(filePath)),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append(' ');
            }
        } catch (final IOException e) {
            log.error("read(error) -> file:{}\nreason:{}", filePath, e.getMessage());
        }
        log.debug("read -> file:{}", filePath);
        return resultStringBuilder.toString();
    }
}