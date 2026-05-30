// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.outbox.consts;

public class Headers {
    public static final String CONSUMER_ERROR = "delivery-error";
    public static final String TRACEPARENT = "traceparent";
    public static final String SOURCE_TOPIC_NAME = "source-topic-name";
}
