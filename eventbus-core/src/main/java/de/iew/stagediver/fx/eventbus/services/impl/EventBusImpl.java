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

package de.iew.stagediver.fx.eventbus.services.impl;

import de.iew.stagediver.fx.eventbus.api.EventBusService;
import de.iew.stagediver.fx.eventbus.api.Topic;
import javafx.application.Platform;
import org.apache.commons.beanutils.MethodUtils;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * The implementation of the {@link EventBusService} interface.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 03:44
 */
public class EventBusImpl implements EventBusService {

    private static Logger log = LoggerFactory.getLogger(EventBusImpl.class);

    private final Hashtable<String, Set<AnnotatedObserver>> observers = new Hashtable<>();

    @Override
    public synchronized void register(Object o) {
        final Set<AnnotatedObserver> annotatedObservers = new HashSet<>();
        Inspector.inspectAnnotatedObservers(o, annotatedObservers);

        Collection<AnnotatedObserver> topicObservers;
        for (AnnotatedObserver ao : annotatedObservers) {
            setupTopic(ao);
            setupNotifyInPlatformThread(ao);
            topicObservers = getOrCreateObserversForTopic(ao.getTopic());

            topicObservers.add(ao);
        }
    }

    @Override
    public synchronized void register(String topic, EventHandler eventHandler) {
        final Method eventHandlerMethod = MethodUtils.getAccessibleMethod(eventHandler.getClass(), "handleEvent", Event.class);

        final AnnotatedObserver ao = new AnnotatedObserver(eventHandler, eventHandlerMethod);
        ao.setTopic(topic);

        final Collection<AnnotatedObserver> topicObservers = getOrCreateObserversForTopic(topic);
        topicObservers.add(ao);
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

    @Override
    public synchronized void unregister(String topic, EventHandler eventHandler) {
        final Collection<AnnotatedObserver> observersForTopic = getObserversForTopic(topic);

        for (AnnotatedObserver annotatedObserver : observersForTopic) {
            if (annotatedObserver.equals(eventHandler)) {
                removeObserver(topic, annotatedObserver);
            }
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

    // TODO Die folgenden beiden Methoden sind Unsinn. Das Ganze muss bei der Inspektion ausgewertet werden oder wenigstens einmalig und die Werte als POJO zurückgegeben werden
    public void setupNotifyInPlatformThread(AnnotatedObserver ao) {
        boolean notifyInPlatformThread = Inspector.inspectObserverNotifyInPlatformThread(ao);
        ao.setNotifyInPlatformThread(notifyInPlatformThread);
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
    public synchronized Collection<AnnotatedObserver> getObserversForTopic(final String topic) {
        if (this.observers.containsKey(topic)) {
            return this.observers.get(topic);
        } else {
            return null;
        }
    }

    /**
     * Returns the collection of observers for the specified topic. Creates and registers a new collection if the topic
     * was not registered before.
     *
     * @param topic The non NULL topic
     * @return the observer collection (never NULL)
     */
    public synchronized Collection<AnnotatedObserver> getOrCreateObserversForTopic(final String topic) {
        Collection<AnnotatedObserver> observers = getObserversForTopic(topic);
        if (observers == null) {
            observers = new HashSet<>();
            this.observers.put(topic, (Set<AnnotatedObserver>) observers);
        }
        return observers;
    }

    public void fireEvent(Event event) {
        final String topic = event.getTopic();
        final Collection<AnnotatedObserver> observers = getOrCreateObserversForTopic(topic);
        observers.addAll(getOrCreateObserversForTopic(Topic.ALL_TOPICS));

        for (AnnotatedObserver observer : observers) {
            try {
                notifyIfInPlatformThread(observer, event);
                notifyDefault(observer, event);
            } catch (Throwable throwable) {
                // TODO we should notify someone about this error...new service (EventBusErrorService)
                log.error("Exception notifying observer {} with event {}", observer, event, throwable);
            }
        }
    }

    protected void notifyIfInPlatformThread(AnnotatedObserver observer, Event event) throws Throwable {
        if (observer.isNotifyInPlatformThread()) {
            Platform.runLater(new JavaFXPlatformRunnable(observer, event));
        }
    }

    protected void notifyDefault(AnnotatedObserver observer, Event event) throws Throwable {
        if (!observer.isNotifyInPlatformThread()) {
            observer.notify(event);
        }
    }
}
