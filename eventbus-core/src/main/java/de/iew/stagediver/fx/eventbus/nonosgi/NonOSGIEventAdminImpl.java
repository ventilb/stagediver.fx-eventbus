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

package de.iew.stagediver.fx.eventbus.nonosgi;

import de.iew.stagediver.fx.eventbus.services.impl.EventBusImpl;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the OSGI {@link EventAdmin} interface to provide a non OSGi version of the event admin to
 * Stagediver FX application.
 * <p>
 * Motivation is to use the eventbus library with non OSGi applications as well as with OSGi applications. You must not
 * need to implement to event notification systems.
 * </p>
 * <p>
 * Background: my proof-of-concept application requires database access. But i have trouble integrating Hibernate within
 * an OSGi environment. So i decided to develop the non OSGi version first as a prototype and fight with Hibernate
 * afterwards.
 * </p>
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 28.01.14 - 21:46
 */
public class NonOSGIEventAdminImpl implements EventAdmin {

    private static final Logger log = LoggerFactory.getLogger(NonOSGIEventAdminImpl.class);

    private final EventBusImpl eventBusImpl;

    public NonOSGIEventAdminImpl(final EventBusImpl eventBusImpl) {
        this.eventBusImpl = eventBusImpl;
    }

    @Override
    public void postEvent(Event event) {
        // TODO No use case yet. But i have ideas :-)
        throw new UnsupportedOperationException("NonOSGIEventAdminImpl.postEvent() is not supported");
    }

    @Override
    public void sendEvent(Event event) {
        this.eventBusImpl.fireEvent(event);
    }
}
