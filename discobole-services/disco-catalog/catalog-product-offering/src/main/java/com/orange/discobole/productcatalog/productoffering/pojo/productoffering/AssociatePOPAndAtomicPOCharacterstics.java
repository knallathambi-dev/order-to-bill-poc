// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.productcatalog.productoffering.pojo.AssociatePOPtoOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.PickAtomicProductOfferingCharacteristic;

import java.util.List;

public class AssociatePOPAndAtomicPOCharacterstics {
    @DefaultValue("0")
   private Integer associatePOPMinCardinality=0;
    @DefaultValue("1")
    private Integer associatePOPMaxCardinality=1;
   private List<AssociatePOPtoOperationSpecification> associatePOPtoOperationSpecifications;
    @DefaultValue("0")
    private Integer characteristicMinCardinality=0;
    @DefaultValue("1")
    private Integer characteristicMaxCardinality=1;
   private List<PickAtomicProductOfferingCharacteristic> characteristics;

    public Integer getAssociatePOPMinCardinality() {
        return associatePOPMinCardinality;
    }


    public Integer getAssociatePOPMaxCardinality() {
        return associatePOPMaxCardinality;
    }

    public List<AssociatePOPtoOperationSpecification> getAssociatePOPtoOperationSpecifications() {
        return associatePOPtoOperationSpecifications;
    }

    public void setAssociatePOPtoOperationSpecifications(List<AssociatePOPtoOperationSpecification> associatePOPtoOperationSpecifications) {
        this.associatePOPtoOperationSpecifications = associatePOPtoOperationSpecifications;
    }

    public Integer getCharacteristicMinCardinality() {
        return characteristicMinCardinality;
    }


    public Integer getCharacteristicMaxCardinality() {
        return characteristicMaxCardinality;
    }

    public List<PickAtomicProductOfferingCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<PickAtomicProductOfferingCharacteristic> characteristics) {
        this.characteristics = characteristics;
    }
}
