package com.orange.discobole.productcatalog.productspecification.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CharacteristicTypes {
    OBJECT("ObjectCharacteristic"),STRING("StringCharacteristic"),DATE("DateCharacteristic"),ADDRESS("AddressCharacteristic"),VALIDITY("ValidityCharacteristic");
    private final String type;
    CharacteristicTypes(String type){
        this.type=type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
    public String getType() {
        return type;
    }

    public static CharacteristicTypes fromValue(String value) {
        for (CharacteristicTypes characteristicTypes : CharacteristicTypes.values()) {
            if (String.valueOf(characteristicTypes.type).equals(value))
                return characteristicTypes;
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return type;
    }
}
