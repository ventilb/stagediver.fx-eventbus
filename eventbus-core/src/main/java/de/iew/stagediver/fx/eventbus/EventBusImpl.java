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

package de.iew.stagediver.fx.eventbus;

import de.iew.stagediver.fx.eventbus.api.EventBusService;
import de.iew.stagediver.fx.eventbus.api.Topic;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The implementation of the {@link de.iew.stagediver.fx.eventbus.api.EventBusService} interface.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 12.01.14 - 03:44
 */
@Component
@Service(value = {EventBusService.class, EventHandler.class})
@org.apache.felix.scr.annotations.Properties({
        @Property(name = EventConstants.EVENT_TOPIC, value = "*")
})
public class EventBusImpl implements EventHandler, EventBusService {

    private final Hashtable<String, Set<AnnotatedObserver>> observers = new Hashtable<>();

    @Activate
    public void init() {
        System.out.println("EventBusImpl.init()");
    }

    @Override
    public synchronized void register(Object o) {
        final Set<AnnotatedObserver> annotatedObservers = new HashSet<>();
        Inspector.inspectAnnotatedObservers(o, annotatedObservers);

        Collection<AnnotatedObserver> topicObservers;
        for (AnnotatedObserver ao : annotatedObservers) {
            setupTopic(ao);
            topicObservers = getOrCreateObserversForTopic(ao.getTopic());

            topicObservers.add(ao);
        }
    }

    @Override
    public synchronized void unregister(Object o) {
        final Set<AnnotatedObserver> annotatedObservers = new HashSet<>();
        Inspector.inspectAnnotatedObservers(o, annotatedObservers);

        String topic;
        for (AnnotatedObserver ao : annotatedObservers) {
            topic = Inspector.inspectObserverTopic(ao);
            topic = getTopicIfInspectionWasNull(topic);

            removeObserver(topic, ao);
        }
    }

    /**
     * Removes the observer specified by object {@code o} and event method {@code eventMethod} for the specified topic.
     *
     * @param topic       The non NULL topic
     * @param eventTarget the event target object
     * @param eventMethod the event method
     */
    public void removeObserver(final String topic, final Object eventTarget, final Method eventMethod) {
        removeObserver(topic, new AnnotatedObserver(eventTarget, eventMethod));
    }

    /**
     * Removes the specified observer for the specified topic.
     *
     * @param topic    The non NULL topic
     * @param observer The observer to remove
     */
    public void removeObserver(final String topic, final AnnotatedObserver observer) {
        final Collection<AnnotatedObserver> observers = getObserversForTopic(topic);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public void setupTopic(AnnotatedObserver ao) {
        String topic = Inspector.inspectObserverTopic(ao);
        topic = getTopicIfInspectionWasNull(topic);
        ao.setTopic(topic);
    }

    public String getTopicIfInspectionWasNull(final String inspectedTopic) {
        // TODO topic syntax validation, see OSGi Event for details
        // TODO should we really return ALL_TOPICS if a topic could not be determined
        return (inspectedTopic == null ? Topic.ALL_TOPICS : inspectedTopic);
    }

    /**
     * Returns the collection of observers for the specified topic. Returns NULL if no observers are registered for
     * the specified topic.
     *
     * @param topic The non NULL topic
     * @return the observers or NULL
     */
    public Collection<AnnotatedObserver> getObserversForTopic(final String topic) {
        return this.observers.get(topic);
    }

    /**
     * Returns the collection of observers for the specified topic. Creates and registers a new collection if the topic
     * was not registered before.
     *
     * @param topic The non NULL topic
     * @return the observer collection (never NULL)
     */
    public Collection<AnnotatedObserver> getOrCreateObserversForTopic(final String topic) {
        Collection<AnnotatedObserver> observers = getObserversForTopic(topic);
        if (observers == null) {
            observers = new HashSet<>();
            this.observers.put(topic, (Set<AnnotatedObserver>) observers);
        }
        return observers;
    }

    /**
     * Handles the OSGi event admin event and notifies the interested observers.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(Event event) {
        final String topic = event.getTopic();
        final Collection<AnnotatedObserver> observers;

        synchronized (this) {
            observers = Collections.unmodifiableCollection(getOrCreateObserversForTopic(topic));
        }

        for (AnnotatedObserver observer : observers) {
            observer.notify(event);
        }

    }
}
