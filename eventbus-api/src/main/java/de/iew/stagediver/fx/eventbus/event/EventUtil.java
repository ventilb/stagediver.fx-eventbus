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

import org.osgi.service.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements utility methods to deal with OSGi events.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 20.08.14 - 23:38
 */
public class EventUtil {

    /**
     * Creates a map of all property / value pairs of the specified event.
     *
     * @param event the event
     * @return the map
     */
    public static Map<String, Object> eventPropertiesToMap(final Event event) {
        final Map<String, Object> eventProperties = new HashMap<>();
        for (String propertyName : event.getPropertyNames()) {
            eventProperties.put(propertyName, event.getProperty(propertyName));
        }
        return eventProperties;
    }
}
