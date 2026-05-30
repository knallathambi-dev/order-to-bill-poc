// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;
@Slf4j
@UtilityClass
public class FileCompressor {

    public static Path compressFileToGzip(Path originalFile) throws IOException {
        if (originalFile == null || !Files.exists(originalFile)) {
            throw new IllegalArgumentException("Original file must not be null and must exist: " + originalFile);
        }

        Path compressedFilePath = Paths.get(originalFile.toString() + ".gz");

        try (FileInputStream fis = new FileInputStream(originalFile.toFile());
             FileOutputStream fos = new FileOutputStream(compressedFilePath.toFile());
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, bytesRead);
            }

            log.info("File compressed successfully to: " + compressedFilePath);
        } catch (IOException e) {
            log.error("Error while compressing file: " + e.getMessage());
            throw e;
        }

        return compressedFilePath;
    }
}


