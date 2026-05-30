// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class PaymentRef {
    @JsonProperty("paymentRefId")
    @Valid
    @ReadOnly(true)
    private List<PaymentRefIdentifier> paymentRefIdentifier;

    public List<PaymentRefIdentifier> getPaymentRefIdentifier() {
        return Collections.unmodifiableList(paymentRefIdentifier);
    }

    public void setPaymentRefIdentifier(List<PaymentRefIdentifier> paymentRefIdentifier) {
        this.paymentRefIdentifier = Collections.unmodifiableList(paymentRefIdentifier);
    }
}