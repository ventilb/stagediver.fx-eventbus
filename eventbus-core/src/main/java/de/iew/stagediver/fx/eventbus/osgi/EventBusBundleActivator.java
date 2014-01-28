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
import org.osgi.framework.*;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * The bundle activator. Will be called by the OSGi container upon bundle start and stop.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 13:23
 */
public class EventBusBundleActivator implements BundleActivator, ServiceListener {

    private static Logger log = LoggerFactory.getLogger(EventBusBundleActivator.class);

    // OSGI stuff /////////////////////////////////////////////////////////////

    private BundleContext context;

    private volatile ServiceRegistration<EventBusService> eventBusServiceServiceRegistration;

    private volatile ServiceRegistration<EventHandler> osgiEventHandlerServiceRegistration;

    private volatile ServiceReference<EventAdmin> eventAdminServiceReference;

    // Services ///////////////////////////////////////////////////////////////

    private EventBusImpl eventBusImpl;

    @Override
    public void start(BundleContext context) throws Exception {
        log.debug("de.iew.stagediver.fx.eventbus Bundle starting");

        this.context = context;

        if (this.eventBusImpl == null) {
            this.eventBusImpl = new EventBusImpl();
        }

        if (this.eventBusServiceServiceRegistration == null) {
            this.eventBusServiceServiceRegistration = context.registerService(EventBusService.class, this.eventBusImpl, null);
        }

        this.eventAdminServiceReference = context.getServiceReference(EventAdmin.class);
        registerEventHandlerIfEventAdminIsAvailable(context);

        context.addServiceListener(this, EventAdmin.class.getName());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.debug("de.iew.stagediver.fx.eventbus Bundle stopping");

        context.removeServiceListener(this);

        unregisterOSGIEventHandler();

        if (this.eventBusServiceServiceRegistration != null) {
            this.eventBusServiceServiceRegistration.unregister();
        }

        this.eventBusImpl = null;
        this.eventBusServiceServiceRegistration = null;
        this.eventAdminServiceReference = null;
        this.context = null;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        final ServiceReference<EventAdmin> eventAdminServiceReference = (ServiceReference<EventAdmin>) event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED:
                log.debug(String.format("The service %s is registered", eventAdminServiceReference));
                if (this.eventAdminServiceReference != null) {
                    log.warn(String.format("There is already an %s service registered. Ignoring this event.", EventAdmin.class.getName()));
                } else {
                    this.eventAdminServiceReference = eventAdminServiceReference;
                    registerOSGIEventHandler(this.context);
                }
                break;
            case ServiceEvent.UNREGISTERING:
                log.debug(String.format("The service %s is unregistering", eventAdminServiceReference));
                if (this.eventAdminServiceReference != null) {
                    unregisterOSGIEventHandler();
                    this.eventAdminServiceReference = null;
                }
                break;
        }
    }

    protected void registerEventHandlerIfEventAdminIsAvailable(BundleContext context) {
        if (this.eventAdminServiceReference != null) {
            registerOSGIEventHandler(context);
        }
    }

    protected void registerOSGIEventHandler(BundleContext context) {
        if (this.osgiEventHandlerServiceRegistration == null) {
            final Dictionary<String, Object> eventHandlerProperties = new Hashtable<>();
            eventHandlerProperties.put(EventConstants.EVENT_TOPIC, "*");

            final OSGIEventHandler osgiEventHandler = new OSGIEventHandler(this.eventBusImpl);
            this.osgiEventHandlerServiceRegistration = context.registerService(EventHandler.class, osgiEventHandler, eventHandlerProperties);
        }
    }

    protected void unregisterOSGIEventHandler() {
        if (this.osgiEventHandlerServiceRegistration != null) {
            this.osgiEventHandlerServiceRegistration.unregister();
        }
        this.osgiEventHandlerServiceRegistration = null;
    }
}
