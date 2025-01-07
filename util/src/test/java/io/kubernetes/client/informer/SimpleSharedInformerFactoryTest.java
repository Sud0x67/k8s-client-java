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

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ListMeta;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.CallGeneratorParams;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import io.kubernetes.client.util.generic.options.ListOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleSharedInformerFactoryTest {

  @Mock private CoreV1Api coreV1Api;

  @Mock private GenericKubernetesApi<V1Pod, V1PodList> genericKubernetesApi;
  private ApiClient apiClient= Configuration.getDefaultApiClient().setReadTimeout(0);

  @Test
  void shutdownInformerFactoryInstantlyAfterStarting() throws ApiException, IOException {
    SimpleSharedInformerFactory factory = new SimpleSharedInformerFactory(apiClient);
    SharedInformer<V1Namespace> nsInformer =
        factory.sharedIndexInformerFor(
            (CallGeneratorParams params) -> {
              return coreV1Api.listNamespace()
                      .resourceVersion(params.resourceVersion)
                      .timeoutSeconds(params.timeoutSeconds)
                      .watch(params.watch)
                      .buildCall(null);
            },
            V1Namespace.class,
            V1NamespaceList.class);

    assertThat(nsInformer).isNotNull();
  }

  @Test
  void clusterScopedNewInformerUsingGenericApi() {
    SimpleSharedInformerFactory factory = new SimpleSharedInformerFactory(apiClient);
    SharedInformer<V1Pod> podInformer =
        factory.sharedIndexInformerFor(genericKubernetesApi, V1Pod.class, 0);
    assertThat(podInformer).isNotNull();

    when(genericKubernetesApi.list(any(ListOptions.class)))
        .thenReturn(
            new KubernetesApiResponse<V1PodList>(
                new V1PodList().metadata(new V1ListMeta().resourceVersion("0"))));
    podInformer.run();
    await().timeout(Duration.ofSeconds(2)).until(podInformer::hasSynced);
    verify(genericKubernetesApi, atLeastOnce()).list(any(ListOptions.class));
  }

  @Test
  void namespaceScopedNewInformerUsingGenericApi() {
    SimpleSharedInformerFactory factory = new SimpleSharedInformerFactory(apiClient);
    SharedInformer<V1Pod> podInformer =
        factory.sharedIndexInformerFor(genericKubernetesApi, V1Pod.class, 0, "default");
    assertThat(podInformer).isNotNull();

    when(genericKubernetesApi.list(eq("default"), any(ListOptions.class)))
        .thenReturn(
            new KubernetesApiResponse<V1PodList>(
                new V1PodList().metadata(new V1ListMeta().resourceVersion("0"))));
    podInformer.run();
    await().timeout(Duration.ofSeconds(2)).until(podInformer::hasSynced);
    verify(genericKubernetesApi, atLeastOnce()).list(eq("default"), any(ListOptions.class));
  }
}
