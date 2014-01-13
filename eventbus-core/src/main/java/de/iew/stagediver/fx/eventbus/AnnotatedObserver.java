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

import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Implements a model to hold an observer configuration. An observer requires the target object and the method to call
 * on the target object when the observer is about to be notified.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 12.01.14 - 14:02
 */
public class AnnotatedObserver {
    private static Logger log = LoggerFactory.getLogger(AnnotatedObserver.class);

    private final Object eventTarget;

    private final Method eventMethod;

    private volatile String topic;

    private volatile boolean notifyInPlatformThread = false;

    public AnnotatedObserver(Object eventTarget, Method eventMethod) {
        this.eventTarget = eventTarget;
        this.eventMethod = eventMethod;
    }

    public Method getEventMethod() {
        return this.eventMethod;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isNotifyInPlatformThread() {
        return notifyInPlatformThread;
    }

    public void setNotifyInPlatformThread(boolean notifyInPlatformThread) {
        this.notifyInPlatformThread = notifyInPlatformThread;
    }

    public void notify(Event event) throws Exception {
        // TODO Signatur auswerten
        this.eventMethod.invoke(this.eventTarget, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotatedObserver that = (AnnotatedObserver) o;

        if (eventMethod != null ? !eventMethod.equals(that.eventMethod) : that.eventMethod != null) return false;
        if (eventTarget != null ? !eventTarget.equals(that.eventTarget) : that.eventTarget != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventTarget != null ? eventTarget.hashCode() : 0;
        result = 31 * result + (eventMethod != null ? eventMethod.hashCode() : 0);
        return result;
    }
}
