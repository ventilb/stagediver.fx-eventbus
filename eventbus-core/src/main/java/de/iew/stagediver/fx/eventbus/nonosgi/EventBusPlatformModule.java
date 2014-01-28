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

import de.iew.stagediver.fx.eventbus.api.EventBusService;
import de.iew.stagediver.fx.eventbus.services.impl.EventBusImpl;
import de.qaware.sdfx.nonosgi.PlatformModule;
import org.osgi.service.event.EventAdmin;

/**
 * The Stagediver FX platform module to use when you want to use the eventbus library in non OSGi applications. It
 * registers an instance of the {@link EventAdmin} implementation and the services from this library. You can use this
 * platform module in your own applications as follows:
 * <code>
 * Lookup.init(<Other platform modules>, new EventBusPlatformModule());
 * </code>
 * To fire an event you simply obtain a reference to the {@link EventAdmin}:
 * <code>
 * Event myEvent = <The OSGi event you want to fire>;
 * EventAdmin eventAdmin = lookup.lookup(EventAdmin.class);
 * eventAdmin.sendEvent(myEvent);
 * </code>
 * while <code>lookup</code> is a reference to Stagediver FXs lookup subsystem.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 28.01.14 - 23:32
 */
public class EventBusPlatformModule extends PlatformModule {

    private EventBusImpl eventBusImpl;

    private NonOSGIEventAdminImpl nonOSGIEventAdminImpl;

    @Override
    protected void configure() {
        super.configure();

        this.eventBusImpl = new EventBusImpl();
        this.nonOSGIEventAdminImpl = new NonOSGIEventAdminImpl(this.eventBusImpl);

        bind(EventAdmin.class).toInstance(this.nonOSGIEventAdminImpl);
        bind(EventBusService.class).toInstance(this.eventBusImpl);
    }
}
