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
        long start = System.currentTimeMillis();
        LOGGER.debug("Starting update of Kubernetes resource cache");
        LOGGER.debug("Updating namespaces");
        List<String> namespaces = client.namespaces().list()
                .getItems()
                .stream()
                .map((n) -> n.getMetadata().getName())
                .collect(Collectors.toList());
        ResourceCache.INSTANCE.setNamespaces(namespaces);
        LOGGER.debug("Updating pods");
        ResourceCache.INSTANCE.set("pod", client.pods().list().getItems());
        LOGGER.debug("Updating deployments");
        ResourceCache.INSTANCE.set("deployment", client.extensions().deployments().list().getItems());
        LOGGER.debug("Updating nodes");
        ResourceCache.INSTANCE.set("node", client.nodes().list().getItems());
        LOGGER.debug("Updating services");
        ResourceCache.INSTANCE.set("service", client.services().list().getItems());
        LOGGER.debug("Updating persistent volumes");
        ResourceCache.INSTANCE.set("persistentvolume", client.persistentVolumes().list().getItems());
        long end = System.currentTimeMillis();
        LOGGER.info(String.format("Finished updating Kubernetes resource cache in %d ms", (end - start)));
    }

    @Override
    protected Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, 20, TimeUnit.SECONDS);
    }
}
