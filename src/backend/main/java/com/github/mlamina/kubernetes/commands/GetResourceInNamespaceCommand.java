package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.DeploymentBundle;
import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.ResourceCache;
import io.fabric8.kubernetes.api.model.Job;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetResourceInNamespaceCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        // "from {namespace} get {resourceType} {resourceName}"
        List<String> namespacedResources = ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes();
        String builder = "^from\\s(" +
                StringUtils.join(ResourceCache.INSTANCE.getNamespaces(), "|") +
                ")\\sget\\s(" +
                StringUtils.join(namespacedResources, "|") +
                ")\\s(\\S+)$";
        return Pattern.compile(builder);
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException, CommandExecutionException {
        Matcher m = getRegExp().matcher(getRawCommand());
        if (!m.find())
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        String namespace = m.group(1);
        String resourceType = m.group(2);
        String resourceName = m.group(3);
        switch (resourceType) {
            case "deployment":
                Deployment deployment = client.extensions()
                        .deployments()
                        .inNamespace(namespace)
                        .withName(resourceName).get();
                failIfNull(deployment, resourceType, resourceName, namespace);
                List<Pod> pods = client.pods()
                        .inNamespace(namespace)
                        .withLabels(deployment.getSpec().getSelector().getMatchLabels())
                        .list().getItems();
                return MetaResponse.resource(new DeploymentBundle(deployment, pods), MetaData.TYPE_DEPLOYMENT_BUNDLE);
            case "pod":
                Pod pod = client.pods().inNamespace(namespace).withName(resourceName).get();
                failIfNull(pod, resourceType, resourceName, namespace);
                return MetaResponse.resource(pod, MetaData.TYPE_POD);
            case "service":
                Service service = client.services().inNamespace(namespace).withName(resourceName).get();
                failIfNull(service, resourceType, resourceName, namespace);
                return MetaResponse.resource(service, MetaData.TYPE_SERVICE);
            case "job":
                Job job = client.extensions().jobs().inNamespace(namespace).withName(resourceName).get();
                failIfNull(job, resourceType, resourceName, namespace);
            return MetaResponse.resource(job, MetaData.TYPE_JOB);
            default:
                throw new CommandParseException("Unknown resource: " + resourceType);
        }
    }

    private void failIfNull(Object resource, String resourceType, String resourceName, String namespace) throws CommandExecutionException {
        if (resource == null)
            throw new CommandExecutionException(
                    getRawCommand(),
                    String.format("No %s '%s' found in namespace '%s'", resourceType, resourceName, namespace));
    }

}
