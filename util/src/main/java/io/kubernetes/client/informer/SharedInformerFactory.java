/*
Copyright 2020 The Kubernetes Authors.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package io.kubernetes.client.informer;

import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.informer.cache.Cache;
import io.kubernetes.client.informer.impl.DefaultSharedIndexInformer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/** SharedInformerFactory class constructs and caches informers for api types. */
public class SharedInformerFactory extends SimpleSharedInformerFactory implements SharedInformerManager {

  private SimpleSharedInformerManager simpleSharedInformerManager;

  /** Constructor w/ default thread pool. */
  /** DEPRECATE: In favor of explicit apiClient constructor to avoid misguiding */
  @Deprecated
  public SharedInformerFactory() {
    this(Configuration.getDefaultApiClient().setReadTimeout(0), Executors.newCachedThreadPool());
  }

  /** Constructor w/ api client specified and default thread pool. */
  public SharedInformerFactory(ApiClient apiClient) {
    this(apiClient, Executors.newCachedThreadPool());
  }

  /**
   * Constructor w/ thread pool specified.
   *
   * @param threadPool specified thread pool
   */
  public SharedInformerFactory(ExecutorService threadPool) {
    this(Configuration.getDefaultApiClient().setReadTimeout(0), threadPool);
  }

  /**
   * Constructor w/ api client and thread pool specified.
   *
   * @param client specific api client
   * @param threadPool specified thread pool
   */
  public SharedInformerFactory(ApiClient client, ExecutorService threadPool) {
    super(client);
    if (client.getReadTimeout() != 0) {
      throw new IllegalArgumentException("read timeout of ApiClient must be zero");
    }
    simpleSharedInformerManager = new SimpleSharedInformerManager(threadPool);
  }

  @Override
  public  <ApiType extends KubernetesObject, ApiListType extends KubernetesListObject>
      SharedIndexInformer<ApiType> sharedIndexInformerFor(
          ListerWatcher<ApiType, ApiListType> listerWatcher,
          Class<ApiType> apiTypeClass,
          long resyncPeriodInMillis,
          BiConsumer<Class<ApiType>, Throwable> exceptionHandler) {

    SharedIndexInformer<ApiType> informer =
        new DefaultSharedIndexInformer<>(
            apiTypeClass, listerWatcher, resyncPeriodInMillis, new Cache<>(), exceptionHandler);
    this.simpleSharedInformerManager.updateSharedIndexInformerIfNotPresent(apiTypeClass, informer);
    return informer;
  }
  /**
   * Gets existing shared index informer, return null if the requesting informer is never
   * constructed.
   *
   * @param <ApiType> the type parameter
   * @param apiTypeClass the api type class
   * @return the existing shared index informer
   */
  @Override
  public synchronized <ApiType extends KubernetesObject>
      SharedIndexInformer<ApiType> getExistingSharedIndexInformer(Class<ApiType> apiTypeClass) {
    return this.simpleSharedInformerManager.getExistingSharedIndexInformer(apiTypeClass);
  }

  /** Start all registered informers. */
  @Override
  public synchronized void startAllRegisteredInformers() {
    this.simpleSharedInformerManager.startAllRegisteredInformers();
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
    this.simpleSharedInformerManager.stopAllRegisteredInformers();
  }
}
