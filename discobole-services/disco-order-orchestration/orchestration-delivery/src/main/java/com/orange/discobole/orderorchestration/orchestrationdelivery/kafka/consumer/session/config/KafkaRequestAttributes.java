// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config;
import org.springframework.web.context.request.AbstractRequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaRequestAttributes class.
 *
 * This class extends AbstractRequestAttributes to manage request attributes in a Kafka-based context.
 * It uses an in-memory map to store attributes and provides methods to manage these attributes.
 */
public class KafkaRequestAttributes extends AbstractRequestAttributes {

    private final Map<String, Object> requestAttributes = new HashMap<>();

    /**
     * Retrieves the value of the specified attribute.
     *
     * @param name  the name of the attribute to retrieve
     * @param scope the scope of the attribute (ignored in this implementation)
     * @return the value of the attribute, or null if not found
     */
    @Override
    public Object getAttribute(String name, int scope) {
        return requestAttributes.get(name);
    }

    /**
     * Sets the value of the specified attribute.
     *
     * @param name  the name of the attribute to set
     * @param value the value of the attribute to set
     * @param scope the scope of the attribute (ignored in this implementation)
     */
    @Override
    public void setAttribute(String name, Object value, int scope) {
        requestAttributes.put(name, value);
    }

    /**
     * Removes the specified attribute.
     *
     * @param name  the name of the attribute to remove
     * @param scope the scope of the attribute (ignored in this implementation)
     */
    @Override
    public void removeAttribute(String name, int scope) {
        requestAttributes.remove(name);
    }

    /**
     * Returns the names of all attributes.
     *
     * @param scope the scope of the attributes (ignored in this implementation)
     * @return an array of attribute names
     */
    @Override
    public String[] getAttributeNames(int scope) {
        return requestAttributes.keySet().toArray(new String[0]);
    }

    /**
     * Registers a destruction callback for the specified attribute.
     * This implementation does not support destruction callbacks.
     *
     * @param name     the name of the attribute for which to register a destruction callback
     * @param callback the destruction callback to register
     * @param scope    the scope of the attribute (ignored in this implementation)
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        // No-op for this example
    }

    /**
     * Resolves a reference to a contextual object.
     * This implementation does not support resolving references.
     *
     * @param key the key of the object to resolve
     * @return always null
     */
    @Override
    public Object resolveReference(String key) {
        return null;
    }

    /**
     * Returns the session ID.
     *
     * @return a fixed session ID
     */
    @Override
    public String getSessionId() {
        return "kafka-session";
    }

    /**
     * Returns a mutex for the session.
     *
     * @return this instance
     */
    @Override
    public Object getSessionMutex() {
        return this;
    }

    /**
     * Updates accessed session attributes.
     * This implementation does not support this functionality.
     */
    @Override
    protected void updateAccessedSessionAttributes() {
        // No-op for this example
    }
}
