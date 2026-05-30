// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.stockitem;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.annotation.Generated;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
@ApiModel(description = "StockItem-based ProductSpec characteristic Specification and value are defined from StockItemType characteristic and stockItem characteristic value. In order to select ProductSpec characteristic Specification and value, on the UI all StockItemType characteristic and stockItem characteristic value will be displayed. User selects used ones for this product Spec.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class StockItemCharacteristic {

	@JsonProperty("id")
	private String id;

	@JsonProperty("description")
	private String description;

	@JsonProperty("name")
	private String name;

	@JsonProperty("valueType")
	private String valueType;

	@JsonProperty("validFor")
	private TimePeriod validFor;

	public StockItemCharacteristic id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Unique identifier of the StockItemCharacteristic
	 *
	 * @return id
	 **/
	@ApiModelProperty(required = true, value = "Unique identifier of the StockItemCharacteristic")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StockItemCharacteristic description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * A narrative that explains in detail what the StockItemCharacteristic is
	 *
	 * @return description
	 **/
	@ApiModelProperty(value = "A narrative that explains in detail what the StockItemCharacteristic is")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StockItemCharacteristic name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Name of the StockItemCharacteristic
	 *
	 * @return name
	 **/
	@ApiModelProperty(value = "Name of the StockItemCharacteristic")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StockItemCharacteristic valueType(String valueType) {
		this.valueType = valueType;
		return this;
	}

	/**
	 * A kind of value that the characteristic can take on, such as numeric, text
	 * and so forth
	 *
	 * @return valueType
	 **/
	@ApiModelProperty(value = "A kind of value that the characteristic can take on, such as numeric, text and so forth")
	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public StockItemCharacteristic validFor(TimePeriod validFor) {
		this.validFor = validFor;
		return this;
	}

	/**
	 * Get validFor
	 *
	 * @return validFor
	 **/
	@ApiModelProperty(value = "")
	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StockItemCharacteristic stockItemCharacteristic = (StockItemCharacteristic) o;
		return Objects.equals(this.id, stockItemCharacteristic.id)
				&& Objects.equals(this.description, stockItemCharacteristic.description)
				&& Objects.equals(this.name, stockItemCharacteristic.name)
				&& Objects.equals(this.valueType, stockItemCharacteristic.valueType)
				&& Objects.equals(this.validFor, stockItemCharacteristic.validFor);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, description, name, valueType, validFor);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class StockItemCharacteristic {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
		sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
