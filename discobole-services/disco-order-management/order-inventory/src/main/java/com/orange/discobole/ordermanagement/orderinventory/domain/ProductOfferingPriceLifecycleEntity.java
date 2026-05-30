package com.orange.discobole.ordermanagement.orderinventory.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductOfferingPriceLifecycleEntity {
    LAUNCHED("launched"),
    UNAVAILABLE("unavailable"),
    RETIRED("retired"),
    OBSOLETE("obsolete");
    private final String value;

    ProductOfferingPriceLifecycleEntity(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @JsonCreator
    public static ProductOfferingPriceLifecycleEntity fromValue(String value) {
        for (ProductOfferingPriceLifecycleEntity b : values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
