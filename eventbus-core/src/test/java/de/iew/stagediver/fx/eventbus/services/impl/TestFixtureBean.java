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

import de.iew.stagediver.fx.eventbus.api.Observer;
import org.osgi.service.event.Event;

/**
 * A simple Pojo to act as a test fixture in the {@link de.iew.stagediver.fx.eventbus.services.impl.Inspector} tests.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 14:35
 */
public class TestFixtureBean {

    public void aVoidMethodWithoutAnnotation() {

    }

    @Observer
    public int aMethodReturning5WithAnnotation() {
        return 5;
    }

    @Observer(topic = "TestFixtureBean.anObserverMethodWithAnnotatedTopic")
    public void anObserverMethodWithAnnotatedTopic() {

    }

    @Observer
    public void anObserverMethodWithObjectTopic(TestTopic topic) {

    }

    @Observer
    public void anObserverMethodWithArrayTopic(TestTopic[] topic) {

    }

    @Observer
    public void anObserverMethodWithPrimitiveTopic(int topic) {

    }

    @Observer(notifyInPlatformThread = true)
    public void anObserverMethodWithAnnotatedTopicNotifyInPlatformThread() {

    }

    @Observer(notifyInPlatformThread = false)
    public void anObserverMethodWithAnnotatedTopicNotifyNotInPlatformThread() {

    }

    @Observer(topic = "TestFixtureBean.anObserverMethodWithAnnotatedTopicAndEvent")
    public void anObserverMethodWithAnnotatedTopicAndEvent(Event event) {

    }
}
