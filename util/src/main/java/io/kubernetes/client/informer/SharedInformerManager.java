package io.kubernetes.client.informer;

import io.kubernetes.client.common.KubernetesObject;

/**
 * A SharedInformerManager manages informers simply by apiTypeClass. For
 */
public interface SharedInformerManager {
    <ApiType extends KubernetesObject>
      SharedIndexInformer<ApiType> getExistingSharedIndexInformer(Class<ApiType> apiTypeClass);
    void startAllRegisteredInformers();

    void stopAllRegisteredInformers();

    void stopAllRegisteredInformers(boolean shutdownThreadPool);
}
