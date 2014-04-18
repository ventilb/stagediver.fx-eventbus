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

import java.util.Dictionary;
import java.util.Map;

/**
 * Implements an abstract OSGi event to provide a better API to OSGi events.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.04.14 - 21:10
 */
public abstract class AbstractEvent extends Event {
    public AbstractEvent(final String topic, final Map<String, ?> properties) {
        super(topic, properties);
    }

    public AbstractEvent(final String topic, final Dictionary<String, ?> properties) {
        super(topic, properties);
    }

    public AbstractEvent(final Class eventClass) {
        this(eventClass.getName().replaceAll("\\.", "/"), (Map) null);
    }

    public AbstractEvent(final Class eventClass, final EventParameterMap<?> parameters) {
        this(eventClass.getName().replaceAll("\\.", "/"), parameters);
    }
}
