package com.github.mlamina.kubernetes;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public enum ResourceCache {
    INSTANCE;


    private AtomicReference<List<String>> namespaces = new AtomicReference<>(Lists.newArrayList());

    public List<String> getNamespaces() {
        return namespaces.get();
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces.set(namespaces);
    }
}
