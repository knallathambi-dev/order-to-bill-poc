// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job.enumerate;


import com.orange.discobole.productinventory.dto.v1.ContentTypeEnum;
import lombok.Getter;

@Getter
public enum FileType {
    JSON(".json"), CSV(".csv");
    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public static FileType fromValue(String value) {
        for (FileType status : FileType.values()) {
            if (status.getExtension().replace(".", "").equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

    public static FileType fromValue(ContentTypeEnum contentTypeEnum) {
        return contentTypeEnum != null ? switch (contentTypeEnum) {
            case JSON -> FileType.JSON;
            case CSV -> FileType.CSV;
        } : null;
    }
}
