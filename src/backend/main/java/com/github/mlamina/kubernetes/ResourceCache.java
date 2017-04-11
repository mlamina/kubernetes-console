package com.github.mlamina.kubernetes;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public enum ResourceCache {
    INSTANCE;

    private final List<String> availableNamespacedResourceTypes =
            Collections.unmodifiableList(Lists.newArrayList("deployment", "pod", "service", "job"));
    private final List<String> availableNonNamespacedResourceTypes =
            Collections.unmodifiableList(Lists.newArrayList("node", "persistentvolume"));
    private final AtomicReference<List<String>> namespaces = new AtomicReference<>(Lists.newArrayList());

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
}
