// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.deserialier;

import java.io.IOException;
import java.util.Set;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import jakarta.validation.*;

public class BeanValidationDeserializer extends BeanDeserializer {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4720793449852828788L;
	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public BeanValidationDeserializer(BeanDeserializerBase src) {
        super(src);
    }
    
    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
    	// for sonar issue 
    	
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
    	// for sonar issue
    }
    /**
	 * this method is used as deserializer.
	 * 
	 * @return Object
	 */
    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object instance = super.deserialize(p, ctxt);
        validate(instance);

        return instance;
    }
    
    /**
	 * This method validates json.
	 * 
	 * @param instance
	 */
    private void validate(Object instance) {
        Set<ConstraintViolation<Object>> violations = validator.validate(instance);
        if (!violations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (ConstraintViolation<Object> violation : violations) {
               msg.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append(". ");
            }
            throw new ConstraintViolationException(msg.toString(), violations);
        }
    }
}
