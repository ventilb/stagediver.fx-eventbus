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

import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the {@link java.lang.Runnable} interface to notify observers from another thread than the senders thread.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 13.01.14 - 02:05
 */
public class JavaFXPlatformRunnable implements Runnable {

    private static Logger log = LoggerFactory.getLogger(JavaFXPlatformRunnable.class);

    private final AnnotatedObserver observer;

    private final Event event;

    public JavaFXPlatformRunnable(AnnotatedObserver observer, Event event) {
        this.observer = observer;
        this.event = event;
    }

    @Override
    public void run() {
        try {
            this.observer.notify(this.event);
        } catch (Exception e) {
            log.error("Exception notifying observer {}", this.observer, e);
            // TODO Das Event sollte irgendwie als Fehlerhaft weiter geschickt werden
            // TODO Das Event sollte an einen ErrorHandler weiter geleitet werden, damit der sich darum kümmert.
        }
    }
}
