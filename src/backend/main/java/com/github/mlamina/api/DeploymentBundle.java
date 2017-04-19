package com.github.mlamina.api;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;

import java.util.Collection;

public class DeploymentBundle {

    private Deployment deployment;
    private Collection<Pod> pods;

    public DeploymentBundle(Deployment deployment, Collection<Pod> pods) {
        this.deployment = deployment;
        this.pods = pods;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    public Collection<Pod> getPods() {
        return pods;
    }

    public void setPods(Collection<Pod> pods) {
        this.pods = pods;
    }
}
