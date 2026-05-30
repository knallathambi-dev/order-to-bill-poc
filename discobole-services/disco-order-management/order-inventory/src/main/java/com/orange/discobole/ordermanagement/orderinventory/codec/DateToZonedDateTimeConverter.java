// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.codec;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(@NotNull Date source) {
        return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
    }
}