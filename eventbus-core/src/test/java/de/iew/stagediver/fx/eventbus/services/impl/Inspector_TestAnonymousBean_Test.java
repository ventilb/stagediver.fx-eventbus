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

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Unit-Tests for the {@link Inspector} functions.
 * <p>
 * This test case tests the behaviour of the {@link Inspector} class if some methods are anonymously overridden.
 * </p>
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 01.05.14 - 16:39
 */
public class Inspector_TestAnonymousBean_Test {

    @Test
    public void testInspectAnnotatedObservers() throws Exception {
        // Testfix erstellen
        TestFixtureBean testFixtureBean = new TestFixtureBean() {
            @Override
            public void aVoidMethodWithoutAnnotation() {
                super.aVoidMethodWithoutAnnotation();
            }
        };

        // Test durchführen
        final Set<AnnotatedObserver> inspectedObservers = new HashSet<>();
        Inspector.inspectAnnotatedObservers(testFixtureBean, inspectedObservers);

        // Test auswerten
        assertThat(inspectedObservers, hasSize(8));
    }

    @Test
    public void testInspectAnnotatedObserverMethods() throws Exception {
        // Testfix erstellen
        TestFixtureBean testFixtureBean = new TestFixtureBean() {
            @Override
            public void aVoidMethodWithoutAnnotation() {
                super.aVoidMethodWithoutAnnotation();
            }
        };

        // Test durchführen
        final Set<Method> inspectedObserverMethods = new HashSet<>();
        Inspector.inspectAnnotatedObserverMethods(testFixtureBean, inspectedObserverMethods);

        // Test auswerten
        assertThat(inspectedObserverMethods, hasSize(8));
    }
}