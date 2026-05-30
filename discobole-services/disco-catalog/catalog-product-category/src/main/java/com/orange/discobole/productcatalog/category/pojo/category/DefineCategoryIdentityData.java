// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.pojo.category;

import java.util.List;


import com.orange.discobole.processflow.annotation.DefaultValue;

public class DefineCategoryIdentityData 
{
	private CategoryIdentityData categoryIdentityData;

	private List<DefineSubcategory>  defineSubcategory;

	private List<DefineEntity>  associateProductOffering;
	
	@DefaultValue("1")
	private Integer CategoryIdentityDataMinCardinality;
	@DefaultValue("1")
	private Integer CatgoryIdentityDataMaxCardinality;
	@DefaultValue("0")
	private Integer DefineSubcategoryMinCardinality;   
	@DefaultValue("1")
	private Integer DefineSubcategoryMaxCardinality;
	
	public CategoryIdentityData getCategoryIdentityData() {
		return categoryIdentityData;
	}


	public List<DefineEntity> getAssociateProductOffering() {
		return associateProductOffering;
	}

	public void setAssociateProductOffering(List<DefineEntity> associateProductOffering) {
		this.associateProductOffering = associateProductOffering;
	}

	public void setCategoryIdentityData(CategoryIdentityData categoryIdentityData) {
		this.categoryIdentityData = categoryIdentityData;
	}

	

	public List<DefineSubcategory> getDefineSubcategory() {
		return defineSubcategory;
	}

	public void setDefineSubcategory(List<DefineSubcategory> defineSubcategory) {
		this.defineSubcategory = defineSubcategory;
	}

	public Integer getCategoryIdentityDataMinCardinality() {
		return CategoryIdentityDataMinCardinality;
	}

	public void setCategoryIdentityDataMinCardinality(Integer categoryIdentityDataMinCardinality) {
		CategoryIdentityDataMinCardinality = categoryIdentityDataMinCardinality;
	}

	public Integer getCatgoryIdentityDataMaxCardinality() {
		return CatgoryIdentityDataMaxCardinality;
	}

	public void setCatgoryIdentityDataMaxCardinality(Integer catgoryIdentityDataMaxCardinality) {
		CatgoryIdentityDataMaxCardinality = catgoryIdentityDataMaxCardinality;
	}

	public Integer getDefineSubcategoryMinCardinality() {
		return DefineSubcategoryMinCardinality;
	}

	public void setDefineSubcategoryMinCardinality(Integer defineSubcategoryMinCardinality) {
		DefineSubcategoryMinCardinality = defineSubcategoryMinCardinality;
	}

	public Integer getDefineSubcategoryMaxCardinality() {
		return DefineSubcategoryMaxCardinality;
	}

	public void setDefineSubcategoryMaxCardinality(Integer defineSubcategoryMaxCardinality) {
		DefineSubcategoryMaxCardinality = defineSubcategoryMaxCardinality;
	}

	
	
}
