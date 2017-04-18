package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.ResourceCache;
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
    public MetaResponse execute(KubernetesClient client) throws CommandParseException {
        Matcher m = getRegExp().matcher(getRawCommand());
        if (!m.find())
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        String namespace = m.group(1);
        String resourceType = m.group(2);
        String resourceName = m.group(3);
        switch (resourceType) {
            case "deployment":
                return MetaResponse.resource(
                        client.extensions().deployments().inNamespace(namespace).withName(resourceName).get(),
                        MetaData.TYPE_DEPLOYMENT);
            case "pod":
                return MetaResponse.resource(
                        client.pods().inNamespace(namespace).withName(resourceName).get(),
                        MetaData.TYPE_POD);
            case "service":
                return MetaResponse.resource(
                        client.services().inNamespace(namespace).withName(resourceName).get(),
                        MetaData.TYPE_SERVICE);
            case "job":
                return MetaResponse.resource(
                        client.extensions().jobs().inNamespace(namespace).withName(resourceName).get(),
                        MetaData.TYPE_JOB);
            default:
                throw new CommandParseException("Unknown resource: " + resourceType);
        }
    }

}
