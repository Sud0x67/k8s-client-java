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

import io.kubernetes.client.common.KubernetesObject;

/**
 * A SharedInformerManager manages informers simply by apiTypeClass. For
 */
public interface SharedInformerManager {
    <ApiType extends KubernetesObject>  void updateSharedIndexInformer(Class<ApiType> apiTypeClass, SharedIndexInformer<ApiType> informer);

    <ApiType extends KubernetesObject>  void updateSharedIndexInformerIfNotPresent(Class<ApiType> apiTypeClass, SharedIndexInformer<ApiType> informer);

    <ApiType extends KubernetesObject>
      SharedIndexInformer<ApiType> getSharedIndexInformer(Class<ApiType> apiTypeClass);
    void startAllRegisteredInformers();

    void stopAllRegisteredInformers();

    void stopAllRegisteredInformers(boolean shutdownThreadPool);
}
