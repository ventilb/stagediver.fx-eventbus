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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The bundle activator. Will be called by the OSGi container upon bundle start and stop.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 12.01.14 - 13:23
 */
public class EventBusBundleActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Bundle EventBusBundleActivator started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Bundle EventBusBundleActivator stopped");
    }
}
