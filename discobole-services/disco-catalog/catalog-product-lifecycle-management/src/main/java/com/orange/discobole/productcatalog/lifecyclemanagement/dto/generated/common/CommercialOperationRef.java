package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class CommercialOperationRef {
    @NotBlank
    private String id;

    /** Filled internally – NOT expected from payload */
    private String name;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@baseType")
    private String baseType;

    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    public @NotBlank String getId() {
        return id;
    }

    public void setId(@NotBlank String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @Override
    public String toString() {
        return "CommercialOperationRef{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                '}';
    }
}
