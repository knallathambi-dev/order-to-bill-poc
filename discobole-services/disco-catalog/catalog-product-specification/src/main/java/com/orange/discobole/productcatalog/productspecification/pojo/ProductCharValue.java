// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class ProductCharValue {

	private Boolean isDefault;
	private String rangeInterval;
	private String regex;
	private String unitOfMeasure;
	private TimePeriod validFor;
	@ReadOnly(true)
	private String value;
	private String valueFrom;
	private String valueTo;
	private String valueType;
	private TimeRange timeRange;
	private Boolean isSelectable;
	@JsonProperty("addressId")
	private String addressId;

	@JsonProperty("subUnitNumber")
	private String subUnitNumber;

	@JsonProperty("streetName")
	private String streetName;

	@JsonProperty("postcode")
	private String postcode;

	@JsonProperty("city")
	private String city;

	@JsonProperty("country")
	private String country;

//	@NotBlank
	@JsonProperty("serviceSpecification.characteristic.value")
	private String serviceSpecCharacteristicReferenceValue = null;

	public Boolean isIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getRangeInterval() {
		return rangeInterval;
	}

	public void setRangeInterval(String rangeInterval) {
		this.rangeInterval = rangeInterval;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueFrom() {
		return valueFrom;
	}

	public void setValueFrom(String valueFrom) {
		this.valueFrom = valueFrom;
	}

	public String getValueTo() {
		return valueTo;
	}

	public void setValueTo(String valueTo) {
		this.valueTo = valueTo;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public Boolean getIsSelectable() {
		return isSelectable;
	}

	public void setIsSelectable(Boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	public String getServiceSpecCharacteristicReferenceValue() {
		return serviceSpecCharacteristicReferenceValue;
	}

	public void setServiceSpecCharacteristicReferenceValue(String serviceSpecCharacteristicReferenceValue) {
		this.serviceSpecCharacteristicReferenceValue = serviceSpecCharacteristicReferenceValue;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	public Boolean getDefault() {
		return isDefault;
	}

	public void setDefault(Boolean aDefault) {
		isDefault = aDefault;
	}

	public Boolean getSelectable() {
		return isSelectable;
	}

	public void setSelectable(Boolean selectable) {
		isSelectable = selectable;
	}

	public String getSubUnitNumber() {
		return subUnitNumber;
	}

	public void setSubUnitNumber(String subUnitNumber) {
		this.subUnitNumber = subUnitNumber;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
