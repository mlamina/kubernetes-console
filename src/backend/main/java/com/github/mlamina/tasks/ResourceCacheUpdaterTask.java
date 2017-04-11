package com.github.mlamina.tasks;

import com.github.mlamina.kubernetes.ResourceCache;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ResourceCacheUpdaterTask extends AbstractScheduledService {

    private final KubernetesClient client = new DefaultKubernetesClient();
    private final Logger LOGGER = LoggerFactory.getLogger(ResourceCacheUpdaterTask.class);

    @Override
    protected void runOneIteration() throws Exception {
        LOGGER.info("Starting update of Kubernetes resource cache");
        LOGGER.info("Updating namespaces");
        List<String> namespaces = client.namespaces().list()
                .getItems()
                .stream()
                .map((n) -> n.getMetadata().getName())
                .collect(Collectors.toList());
        ResourceCache.INSTANCE.setNamespaces(namespaces);
        LOGGER.info("Finished updating Kubernetes resource cache");
    }

    @Override
    protected Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, 20, TimeUnit.SECONDS);
    }
}
