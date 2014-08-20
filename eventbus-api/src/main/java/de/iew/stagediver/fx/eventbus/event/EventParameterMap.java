/*
 * Copyright 2012-2014 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.stagediver.fx.eventbus.event;

import de.iew.framework.utils.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a simple extension of {@link java.util.HashMap} to provide a fluent interface to build parameter maps
 * for OSGis event system.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.04.14 - 21:14
 */
public class EventParameterMap<T> extends HashMap<String, T> implements Serializable {

    private static final long serialVersionUID = -4260520632450829459L;

    /**
     * Adds the specified property and value into this parameter map.
     *
     * @param property Not NULL property name
     * @param value    The value to add (Can be NULL)
     * @return This parameter map for fluent interface
     */
    public EventParameterMap<T> add(final String property, final T value) {
        Assert.assertMethodParameterIsNotNull(property);

        put(property, value);
        return this;
    }

    /**
     * Adds all properties of the specified map into this parameter map.
     *
     * @param properties The properties to add
     * @return This parameter map for fluent interface
     */
    public EventParameterMap<T> putProperties(final Map<String, T> properties) {
        if (properties != null) {
            putAll(properties);
        }
        return this;
    }

    /**
     * Many use cases just put a few values into a parameter map. This method shortens the creation of such maps because
     * you can provide the first property / value pair at construction time.
     *
     * @param property Not NULL property name
     * @param value    The value to add (Can be NULL)
     * @return The created parameter map
     */
    public static EventParameterMap<Object> create(final String property, final Object value) {
        return create().add(property, value);
    }

    /**
     * Many use cases just put a few values into a parameter map. This method creates an empty parameter map.
     *
     * @return The created parameter map
     */
    public static EventParameterMap<Object> create() {
        return new EventParameterMap<>();
    }

    /**
     * Many use cases just put a few values into a parameter map. This method shortens the creation of such maps because
     * you can put a map of properties at construction time.
     *
     * @param properties the map of properties
     * @return The created parameter map
     */
    public static EventParameterMap<Object> create(final Map<String, Object> properties) {
        return create().putProperties(properties);
    }
}
