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

package de.iew.stagediver.fx.eventbus

import de.iew.stagediver.fx.eventbus.api.Topic
import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.hamcrest.CoreMatchers
import org.hamcrest.collection.IsMapContaining
import org.osgi.service.event.Event

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.junit.Assert.assertThat

/**
 * Unit-Tests for the {@link EventBusImpl} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 16:25
 */
class EventBusImplTest extends GroovyTestCase {
    def EventBusImpl testee;

    def TestFixtureBean testFixtureBean;

    void testHandleEventDispatch() {
        // Testfix erstellen
        def Event eventToFire = new Event("TestFixtureBean/anObserverMethodWithAnnotatedTopicAndEvent", Collections.emptyMap())

        def eventMethod = TestFixtureBean.class.getMethod("anObserverMethodWithAnnotatedTopicAndEvent", Event.class)
        def AnnotatedObserver obs1 = new AnnotatedObserver(testFixtureBean, eventMethod)

        // Test durchführen
        def notifyIfInPlatformThreadWasCalled = false
        def notifyDefaultWasCalled = false

        def eventBus = [
                notifyIfInPlatformThread: { observer, event -> notifyIfInPlatformThreadWasCalled = true },
                notifyDefault: { observer, event -> notifyDefaultWasCalled = true },
                getOrCreateObserversForTopic: { topic -> [obs1] }
        ] as EventBusImpl
        eventBus.handleEvent(eventToFire)

        // Test auswerten
        /// Beide Dispatcher werden aufgerufen, weil die if-Anweisung der nicht greift, da wir die Methoden überladen
        /// haben.
        assertThat(notifyIfInPlatformThreadWasCalled, is(true))
        assertThat(notifyDefaultWasCalled, is(true))
    }

    void testHandleEvent() {
        // Testfix erstellen
        def dispatchedEvents = [:]

        def testFixtureBean1 = [anObserverMethodWithAnnotatedTopicAndEvent: { event -> dispatchedEvents["testFixtureBean1"] = event }] as TestFixtureBean
        def testFixtureBean2 = [anObserverMethodWithAnnotatedTopicAndEvent: { event -> dispatchedEvents["testFixtureBean2"] = event }] as TestFixtureBean
        def eventMethod = testFixtureBean1.getClass().getMethod("anObserverMethodWithAnnotatedTopicAndEvent", Event.class)

        def AnnotatedObserver obs1 = new AnnotatedObserver(testFixtureBean1, eventMethod)
        def AnnotatedObserver obs2 = new AnnotatedObserver(testFixtureBean2, eventMethod)

        def Event eventToFire = new Event("TestFixtureBean/anObserverMethodWithAnnotatedTopicAndEvent", Collections.emptyMap())

        // Test durchführen
        def EventBusImpl eventBus = [
                getOrCreateObserversForTopic: { topic -> [obs1, obs2] }
        ] as EventBusImpl
        eventBus.handleEvent(eventToFire)

        // Test auswerten
        assertThat dispatchedEvents, IsMapContaining.hasEntry("testFixtureBean1", eventToFire)
        assertThat dispatchedEvents, IsMapContaining.hasEntry("testFixtureBean2", eventToFire)
    }

    void testRegister() {
        // Testfix erstellen

        // Test durchführen
        testee.register(testFixtureBean)

        // Test auswerten
        assertThat testee.getObserversForTopic("TestFixtureBean.anObserverMethodWithAnnotatedTopic"), hasSize(1)
        assertThat testee.getObserversForTopic("de/iew/stagediver/fx/eventbus/TestTopic"), hasSize(1)
        assertThat testee.getObserversForTopic(Topic.ALL_TOPICS), hasSize(5)
    }

    void testUnregister() {
        // Testfix erstellen
        testee.register(testFixtureBean)

        // Test durchführen
        testee.unregister(testFixtureBean)

        // Test auswerten
        assertThat testee.getObserversForTopic("TestFixtureBean.anObserverMethodWithAnnotatedTopic"), hasSize(0)
        assertThat testee.getObserversForTopic("de/iew/stagediver/fx/eventbus/TestTopic"), hasSize(0)
        assertThat testee.getObserversForTopic(Topic.ALL_TOPICS), hasSize(0)
    }

    void testRemoveObserver() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")

        testee.register(testFixtureBean)

        // Test durchführen
        testee.removeObserver("TestFixtureBean.anObserverMethodWithAnnotatedTopic", testFixtureBean, eventMethod)

        // Test auswerten
        assertThat testee.getObserversForTopic("TestFixtureBean.anObserverMethodWithAnnotatedTopic"), hasSize(0)
        assertThat testee.getObserversForTopic("de/iew/stagediver/fx/eventbus/TestTopic"), hasSize(1)
        assertThat testee.getObserversForTopic(Topic.ALL_TOPICS), hasSize(5)
    }

    @Override
    void setUp() {
        super.setUp();
        testee = new EventBusImpl()
        testFixtureBean = new TestFixtureBean()
    }
}
