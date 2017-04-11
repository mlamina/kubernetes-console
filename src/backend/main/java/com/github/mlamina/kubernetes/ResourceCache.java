package com.github.mlamina.kubernetes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.fabric8.kubernetes.api.model.HasMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public enum ResourceCache {
    INSTANCE;

    private final List<String> availableNamespacedResourceTypes =
            Collections.unmodifiableList(Lists.newArrayList("deployment", "pod", "service"));
    private final List<String> availableNonNamespacedResourceTypes =
            Collections.unmodifiableList(Lists.newArrayList("node", "persistentvolume"));

    private final AtomicReference<List<String>> namespaces = new AtomicReference<>(Lists.newArrayList());
    private final Map<String, List<? extends HasMetadata>> cache = Maps.newConcurrentMap();

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCache.class);

    public List<String> getNamespaces() {
        return namespaces.get();
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces.set(namespaces);
    }

    public List<String> getAvailableNamespacedResourceTypes() {
        return Lists.newArrayList(availableNamespacedResourceTypes);
    }

    public List<String> getAvailableNonNamespacedResourceTypes() {
        return Lists.newArrayList(availableNonNamespacedResourceTypes);
    }

    public <T extends HasMetadata> void set(String resource, List<T> resources) {
        cache.put(resource, resources);
    }

    public List<? extends HasMetadata> get(String resource) {
        if (!cache.containsKey(resource))
            LOGGER.warn("No cached resources for: " + resource);
        return cache.getOrDefault(resource, Lists.newArrayList());
    }
}
