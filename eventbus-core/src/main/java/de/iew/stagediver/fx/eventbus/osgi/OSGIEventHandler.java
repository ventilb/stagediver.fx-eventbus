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

package de.iew.stagediver.fx.eventbus.osgi;

import de.iew.stagediver.fx.eventbus.api.EventBusService;
import de.iew.stagediver.fx.eventbus.services.impl.EventBusImpl;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a bridge between the OSGI event handler world and our event bus world. This handler intercepts OSGI events
 * fired by an {@link EventAdmin} and directs the event to the {@link EventBusService} for further processing.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 28.01.14 - 22:24
 */
public class OSGIEventHandler implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(OSGIEventHandler.class);

    private final EventBusImpl eventBusImpl;

    public OSGIEventHandler(final EventBusImpl eventBusImpl) {
        this.eventBusImpl = eventBusImpl;
    }

    @Override
    public void handleEvent(Event event) {
        this.eventBusImpl.fireEvent(event);
    }
}
