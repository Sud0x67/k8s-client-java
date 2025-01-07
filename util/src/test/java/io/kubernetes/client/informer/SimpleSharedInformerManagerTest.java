/*
 * Copyright 2025 The Kubernetes Authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kubernetes.client.informer;

import com.google.common.util.concurrent.MoreExecutors;
import io.kubernetes.client.common.KubernetesObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleSharedInformerManagerTest {

    private SimpleSharedInformerManager simpleSharedInformerManagerUnderTest;

    @BeforeEach
    void setUp() {
        simpleSharedInformerManagerUnderTest = new SimpleSharedInformerManager(
                MoreExecutors.newDirectExecutorService());
    }

    @Test
    void testUpdateSharedIndexInformer() {
        // Setup
        final SharedIndexInformer<KubernetesObject> informer = null;

        // Run the test
        simpleSharedInformerManagerUnderTest.updateSharedIndexInformer(KubernetesObject.class, informer);

        // Verify the results
    }

    @Test
    void testUpdateSharedIndexInformerIfNotPresent() {
        // Setup
        final SharedIndexInformer<KubernetesObject> informer = null;

        // Run the test
        simpleSharedInformerManagerUnderTest.updateSharedIndexInformerIfNotPresent(KubernetesObject.class, informer);

        // Verify the results
    }

    @Test
    void testGetExistingSharedIndexInformer() {
        // Setup
        // Run the test
        final SharedIndexInformer<KubernetesObject> result = simpleSharedInformerManagerUnderTest.getSharedIndexInformer(
                KubernetesObject.class);

        // Verify the results
    }

    @Test
    void testStartAllRegisteredInformers() {
        // Setup
        // Run the test
        simpleSharedInformerManagerUnderTest.startAllRegisteredInformers();

        // Verify the results
    }

    @Test
    void testStopAllRegisteredInformers1() {
        // Setup
        // Run the test
        simpleSharedInformerManagerUnderTest.stopAllRegisteredInformers();

        // Verify the results
    }

    @Test
    void testStopAllRegisteredInformers2() {
        // Setup
        // Run the test
        simpleSharedInformerManagerUnderTest.stopAllRegisteredInformers(false);

        // Verify the results
    }
}
