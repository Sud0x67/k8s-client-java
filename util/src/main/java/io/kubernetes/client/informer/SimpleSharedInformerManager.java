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

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.common.KubernetesObject;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SimpleSharedInformerManager implements SharedInformerManager {
    private Map<Type, SharedIndexInformer> informers = new HashMap<>();

    private Map<Type, Future> startedInformers = new HashMap<>();
    private ExecutorService informerExecutor;

    public SimpleSharedInformerManager(ExecutorService informerExecutor) {
        this.informerExecutor = informerExecutor;
    }

    public synchronized  <ApiType extends KubernetesObject>  void updateSharedIndexInformer(Class<ApiType> apiTypeClass, SharedIndexInformer<ApiType> informer) {
        Type apiType = TypeToken.get(apiTypeClass).getType();
        SharedInformer<ApiType> oldInformer = informers.remove(apiType);
        if(oldInformer != null && startedInformers.remove(apiType) != null) {
            informer.stop();
        }
        informers.put(apiTypeClass, informer);
    }
    public synchronized  <ApiType extends KubernetesObject>  void updateSharedIndexInformerIfNotPresent(Class<ApiType> apiTypeClass, SharedIndexInformer<ApiType> informer) {
        Type apiType = TypeToken.get(apiTypeClass).getType();
        if(informers.get(apiType) == null) {
            informers.put(apiTypeClass, informer);
        }
    }
    @Override
    public synchronized <ApiType extends KubernetesObject>
    SharedIndexInformer<ApiType> getExistingSharedIndexInformer(Class<ApiType> apiTypeClass) {
        return this.informers.get(TypeToken.get(apiTypeClass).getType());
    }

    /** Start all registered informers. */
    @Override
    public synchronized void startAllRegisteredInformers() {
        if (MapUtils.isEmpty(informers)) {
            return;
        }
        informers.forEach(
                (informerType, informer) ->
                        startedInformers.computeIfAbsent(
                                informerType, key -> informerExecutor.submit(informer::run)));
    }

    /** Stop all registered informers and shut down the thread pool. */
    @Override
    public synchronized void stopAllRegisteredInformers() {
        stopAllRegisteredInformers(true);
    }

    /**
     * Stop all registered informers.
     *
     * @param shutdownThreadPool whether or not to shut down the thread pool.
     */
    @Override
    public synchronized void stopAllRegisteredInformers(boolean shutdownThreadPool) {
        if (MapUtils.isEmpty(informers)) {
            return;
        }
        informers.forEach(
                (informerType, informer) -> {
                    if (startedInformers.remove(informerType) != null) {
                        informer.stop();
                    }
                });
        if (shutdownThreadPool) {
            informerExecutor.shutdown();
        }
    }
}
