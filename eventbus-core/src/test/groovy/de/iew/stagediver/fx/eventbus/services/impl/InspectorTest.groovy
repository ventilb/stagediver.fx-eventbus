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

package de.iew.stagediver.fx.eventbus.services.impl

import java.lang.reflect.Method

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.Matchers.is as IS
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.collection.IsCollectionWithSize.hasSize

/**
 * Unit-Tests for the {@link Inspector} functions.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 14:22
 */
class InspectorTest extends GroovyTestCase {

    // Testfixture objekt
    def TestFixtureBean testFixtureBean

    void testInspectObserverNotifyInPlatformThread_with_notification_in_platform_thread() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopicNotifyInPlatformThread")
        def observer = new AnnotatedObserver(testFixtureBean, eventMethod)

        // Test durchführen
        def actualNotifyInPlatformThread = Inspector.inspectObserverNotifyInPlatformThread(observer)

        // Test auswerten
        assertThat actualNotifyInPlatformThread, IS(true)
    }

    void testInspectObserverNotifyInPlatformThread_without_notification_in_platform_thread() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopicNotifyNotInPlatformThread")
        def observer = new AnnotatedObserver(testFixtureBean, eventMethod)

        // Test durchführen
        def actualNotifyInPlatformThread = Inspector.inspectObserverNotifyInPlatformThread(observer)

        // Test auswerten
        assertThat actualNotifyInPlatformThread, IS(false)
    }

    void testInspectAnnotatedObservers() {
        // Testfix erstellen

        // Test durchführen
        def observableMethods = [] as Set<AnnotatedObserver>
        Inspector.inspectAnnotatedObservers(testFixtureBean, observableMethods)

        // Test auswerten
        assertThat observableMethods.size(), IS(8)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "aMethodReturning5WithAnnotation" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithAnnotatedTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithObjectTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithArrayTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithPrimitiveTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithAnnotatedTopicNotifyInPlatformThread" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithAnnotatedTopicNotifyNotInPlatformThread" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.eventMethod.name == "anObserverMethodWithAnnotatedTopicAndEvent" }, hasSize(1)
    }

    void testInspectAnnotatedObserverMethods() {
        // Testfix erstellen

        // Test durchführen
        def observableMethods = [] as Set<Method>
        Inspector.inspectAnnotatedObserverMethods(testFixtureBean, observableMethods)

        // Test auswerten
        assertThat observableMethods.size(), IS(8)
        assertThat observableMethods.findAll { m -> m.name == "aMethodReturning5WithAnnotation" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithAnnotatedTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithObjectTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithArrayTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithPrimitiveTopic" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithAnnotatedTopicNotifyInPlatformThread" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithAnnotatedTopicNotifyNotInPlatformThread" }, hasSize(1)
        assertThat observableMethods.findAll { m -> m.name == "anObserverMethodWithAnnotatedTopicAndEvent" }, hasSize(1)
    }

    void testGetObserverAnnotation() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")

        // Test durchführen
        de.iew.stagediver.fx.eventbus.api.Observer observer = Inspector.getObserverAnnotation(eventMethod)

        // Test auswerten
        assertThat observer, IS(notNullValue())
        assertThat observer.topic(), IS("TestFixtureBean.anObserverMethodWithAnnotatedTopic")
    }

    void testInspectObserverTopicByAnnotation() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")

        // Test durchführen
        def actualTopic = new String[1]
        Inspector.inspectObserverTopicByAnnotation(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS("TestFixtureBean.anObserverMethodWithAnnotatedTopic")
    }

    void testInspectObserverTopicByAnnotation_dont_overwrite_determined_topic() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")
        def alreadyDeterminedTopic = "an.already.determined.topic"

        // Test durchführen
        def actualTopic = [alreadyDeterminedTopic] as String[]

        Inspector.inspectObserverTopicByAnnotation(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS(alreadyDeterminedTopic)
    }

    void testInspectObserverTopicByMethodSignature_with_object_topic() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithObjectTopic", TestTopic.class)

        // Test durchführen
        def actualTopic = new String[1]

        Inspector.inspectObserverTopicByMethodSignature(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS("de/iew/stagediver/fx/eventbus/services/impl/TestTopic")
    }

    void testInspectObserverTopicByMethodSignature_with_array_topic() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithArrayTopic", TestTopic[].class)

        // Test durchführen
        def actualTopic = new String[1]

        Inspector.inspectObserverTopicByMethodSignature(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS(nullValue())
    }

    void testInspectObserverTopicByMethodSignature_with_primitive_topic() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithPrimitiveTopic", Integer.TYPE)

        // Test durchführen
        def actualTopic = new String[1]

        Inspector.inspectObserverTopicByMethodSignature(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS(nullValue())
    }

    void testInspectObserverTopicByMethodSignature_dont_overwrite_determined_topic() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithObjectTopic", TestTopic.class)
        def alreadyDeterminedTopic = "an.already.determined.topic"

        // Test durchführen
        def actualTopic = [alreadyDeterminedTopic] as String[]

        Inspector.inspectObserverTopicByMethodSignature(eventMethod, actualTopic)

        // Test auswerten
        assertThat actualTopic[0], IS(alreadyDeterminedTopic)
    }

    /**
     * Integrative test to test the topic inspection use case.
     */
    void testInspectObserverTopic_with_annotated_method() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")
        def observer = new AnnotatedObserver(testFixtureBean, eventMethod)

        // Test durchführen
        def actualTopic = Inspector.inspectObserverTopic(observer)

        // Test auswerten
        assertThat actualTopic, IS("TestFixtureBean.anObserverMethodWithAnnotatedTopic")
    }

    /**
     * Integrative test to test the topic inspection use case.
     */
    void testInspectObserverTopic_with_parameter_types() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithObjectTopic", TestTopic.class)
        def observer = new AnnotatedObserver(testFixtureBean, eventMethod)

        // Test durchführen
        def actualTopic = Inspector.inspectObserverTopic(observer)

        // Test auswerten
        assertThat actualTopic, IS("de/iew/stagediver/fx/eventbus/services/impl/TestTopic")
    }

    /**
     * Integrative test to test the topic inspection use case.
     */
    void testInspectTopic_with_annotated_method() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithAnnotatedTopic")

        // Test durchführen
        def actualTopic = Inspector.inspectTopic(eventMethod)

        // Test auswerten
        assertThat actualTopic, IS("TestFixtureBean.anObserverMethodWithAnnotatedTopic")
    }

    /**
     * Integrative test to test the topic inspection use case.
     */
    void testInspectTopic_with_parameter_types() {
        // Testfix erstellen
        def eventMethod = testFixtureBean.getClass().getMethod("anObserverMethodWithObjectTopic", TestTopic.class)

        // Test durchführen
        def actualTopic = Inspector.inspectTopic(eventMethod)

        // Test auswerten
        assertThat actualTopic, IS("de/iew/stagediver/fx/eventbus/services/impl/TestTopic")
    }

    @Override
    void setUp() {
        super.setUp();

        testFixtureBean = new TestFixtureBean()
    }

}
