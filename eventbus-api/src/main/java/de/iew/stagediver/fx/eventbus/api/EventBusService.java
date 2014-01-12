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

package de.iew.stagediver.fx.eventbus.api;

/**
 * Declares a service interface to implement an event bus.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 12.01.14 - 13:06
 */
public interface EventBusService {

    /**
     * Registers the specified object as an observer to events.
     * <p>
     * The object is inspected and each method with an {@link de.iew.stagediver.fx.eventbus.api.Observer} annotation
     * is registered as a topic.
     * </p>
     *
     * @param o The object to register
     */
    public void register(Object o);

    /**
     * Removes the specified object from this event bus.
     * <p>
     * Removes all registered topics. The object will no longer receive events by this event bus.
     * </p>
     *
     * @param o The object to unregister
     */
    public void unregister(Object o);

}
