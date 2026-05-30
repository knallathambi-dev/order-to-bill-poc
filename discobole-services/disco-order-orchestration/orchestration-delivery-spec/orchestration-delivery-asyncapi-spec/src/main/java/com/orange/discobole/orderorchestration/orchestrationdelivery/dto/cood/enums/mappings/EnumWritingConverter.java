// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.mappings;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * this class convert enum pojo value to value that inserted inside the document
 * @param <T> T should be extending Enum
 */

@WritingConverter
class EnumWritingConverter<T extends Enum> implements Converter<T, String> {
    @Override
    public String convert(T source) {
        return source.toString();
    }

    @WritingConverter
    public static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime offsetDateTime) {
            return Date.from(offsetDateTime.toInstant());
        }
    }
}