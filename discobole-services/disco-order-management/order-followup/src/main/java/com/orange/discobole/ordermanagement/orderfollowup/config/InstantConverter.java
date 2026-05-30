// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
@WritingConverter
public class InstantConverter implements Converter<OffsetDateTime, Instant> {

    @Override
    public Instant convert(OffsetDateTime source) {
        return source.toInstant();
    }
}