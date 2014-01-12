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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.junit.Assert.assertThat

/**
 * Unit-Tests for the {@link EventBusImpl} implementation.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 12.01.14 - 16:25
 */
class EventBusImplTest extends GroovyTestCase {
    def EventBusImpl testee;

    def TestFixtureBean testFixtureBean;

    void testRegister() {
        // Testfix erstellen

        // Test durchführen
        testee.register(testFixtureBean)

        // Test auswerten
        assertThat testee.getObserversForTopic("TestFixtureBean.anObserverMethodWithAnnotatedTopic"), hasSize(1)
        assertThat testee.getObserversForTopic(TestTopic.class.getName()), hasSize(1)
        assertThat testee.getObserversForTopic(Topic.ALL_TOPICS), hasSize(3)
    }

    void testUnregister() {
        // Testfix erstellen
        testee.register(testFixtureBean)

        // Test durchführen
        testee.unregister(testFixtureBean)

        // Test auswerten
        assertThat testee.getObserversForTopic("TestFixtureBean.anObserverMethodWithAnnotatedTopic"), hasSize(0)
        assertThat testee.getObserversForTopic(TestTopic.class.getName()), hasSize(0)
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
        assertThat testee.getObserversForTopic(TestTopic.class.getName()), hasSize(1)
        assertThat testee.getObserversForTopic(Topic.ALL_TOPICS), hasSize(3)
    }

    @Override
    void setUp() {
        super.setUp();
        testee = new EventBusImpl()
        testFixtureBean = new TestFixtureBean()
    }
}
