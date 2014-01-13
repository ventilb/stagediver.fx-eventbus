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

import de.iew.stagediver.fx.eventbus.api.Observer;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Inspector class to extract the information required to setup observers and topics for objects.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 14:02
 */
public class Inspector {

    public static void inspectAnnotatedObservers(final Object o, final Set<AnnotatedObserver> inspectedObservers) {
        final Class classToInspect = o.getClass();
        final Method[] methods = classToInspect.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Observer.class)) {
                inspectedObservers.add(new AnnotatedObserver(o, method));
            }
        }
    }

    public static void inspectAnnotatedObserverMethods(final Object o, final Set<Method> inspectedMethods) {
        final Class classToInspect = o.getClass();
        final Method[] methods = classToInspect.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Observer.class)) {
                inspectedMethods.add(method);
            }
        }
    }

// Irgendwie kriege ich das Language Level nicht auf 8 geändert
//    public static void inspectAnnotatedObservers(final Object o, final Set<AnnotatedObserver> inspectedObservers) {
//        inspectAnnotatedObserverMethodsImpl(o, (ob, m) -> inspectedObservers.add(new AnnotatedObserver(ob, m)));
//    }
//
//    public static void inspectAnnotatedObserverMethods(final Object o, final Set<Method> inspectedMethods) {
//        inspectAnnotatedObserverMethodsImpl(o, (ob, m) -> inspectedMethods.add(m));
//    }
//
//    protected static interface Lambda {
//        void call(Object o, Method m);
//    }
//
//    protected static void inspectAnnotatedObserverMethodsImpl(final Object o, final Lambda lambda) {
//        final Class classToInspect = o.getClass();
//        final Method[] methods = classToInspect.getDeclaredMethods();
//
//        for (Method method : methods) {
//            if (method.isAnnotationPresent(Observer.class)) {
//                lambda.call(o, method);
//            }
//        }
//    }

    /**
     * Inspects the topic of the given observer. The inspection is delegated to {@link #inspectTopic(java.lang.reflect.Method)}.
     *
     * @param observer the observer to inspect
     * @return the determined topic or NULL
     * @see #inspectTopic(java.lang.reflect.Method)
     */
    public static String inspectObserverTopic(final AnnotatedObserver observer) {
        return inspectTopic(observer.getEventMethod());
    }

    /**
     * Inspects the topic in the given method.
     * <p>
     * The inspection is done in the following order:
     * </p>
     * <ol>
     * <li>Inspect the {@link de.iew.stagediver.fx.eventbus.api.Observer#topic()} property</li>
     * <li>Inspect the method signature</li>
     * </ol>
     * <p>
     * The inspections stops as soon as a topic was determined. This method returns NULL if a topic could not be
     * determined.
     * </p>
     *
     * @param eventMethod the method to inspect
     * @return the determined topic or NULL
     */
    public static String inspectTopic(final Method eventMethod) {
        final String[] topicHolder = new String[1];

        inspectObserverTopicByAnnotation(eventMethod, topicHolder);
        inspectObserverTopicByMethodSignature(eventMethod, topicHolder);

        return topicHolder[0];
    }

    public static Observer getObserverAnnotation(final Method eventMethod) {
        final Observer annotation = eventMethod.getAnnotation(Observer.class);
        return annotation;
    }

    public static void inspectObserverTopicByAnnotation(final Method eventMethod, final String[] topicHolder) {
        if (isNotBlank(topicHolder[0])) {
            return;
        }
        final Observer annotation = getObserverAnnotation(eventMethod);

        String topic = annotation.topic();

        if (isBlank(topic)) {
            topic = null;
        }

        topicHolder[0] = topic;
    }

    public static void inspectObserverTopicByMethodSignature(final Method eventMethod, final String[] topicHolder) {
        if (isNotBlank(topicHolder[0])) {
            return;
        }
        final int parameterCount = eventMethod.getParameterCount();
        final Class[] parameterTypes;
        final Class parameterOfInterest;

        if (parameterCount >= 1) {
            parameterTypes = eventMethod.getParameterTypes();
            parameterOfInterest = parameterTypes[0];

            if (!parameterOfInterest.isArray()
                    && !parameterOfInterest.isPrimitive()) {
                topicHolder[0] = parameterOfInterest.getName().replaceAll("\\.", "/");
            }
        }
    }

    /**
     * Returns TRUE if {@code s} is NULL or the empty string, FALSE otherwise.
     *
     * @param s the string to test
     * @return boolean
     */
    protected static boolean isBlank(final String s) {
        return (s == null || "".equals(s));
    }

    /**
     * Returns TRUE if {@code s} is not NULL and is not the empty string, FALSE otherwise.
     *
     * @param s the string to test
     * @return boolean
     */
    protected static boolean isNotBlank(final String s) {
        // Was ist besser, Methodenaufruf und negieren oder De-Morgan anwenden? o_O
        return !isBlank(s);
    }
}
