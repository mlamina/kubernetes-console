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
import java.util.stream.Collectors;

public class GetResourcesInNamespaceCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        // "get {resourceType}s in {namespace}"
        List<String> namespacedResources = ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes()
                .stream()
                .map((r) -> r + "s")
                .collect(Collectors.toList());
        String builder = "^get\\s(" +
                StringUtils.join(namespacedResources, "|") +
                ")\\sin\\s(" +
                StringUtils.join(ResourceCache.INSTANCE.getNamespaces(), "|") +
                ")$";
        return Pattern.compile(builder);
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException {
        try {
            Matcher m = getRegExp().matcher(getRawCommand());
            if (!m.find())
                throw new CommandParseException("Unable to parse command: " + getRawCommand());
            String resource = m.group(1);
            String namespace = m.group(2);
            switch (resource) {
                case "deployments":
                    return MetaResponse.list(client.extensions().deployments().inNamespace(namespace).list().getItems(), MetaData.TYPE_DEPLOYMENT);
                case "pods":
                    return MetaResponse.list(client.pods().inNamespace(namespace).list().getItems(), MetaData.TYPE_POD);
                case "services":
                    return MetaResponse.list(client.services().inNamespace(namespace).list().getItems(), MetaData.TYPE_SERVICE);
                case "jobs":
                    return MetaResponse.list(client.extensions().jobs().inNamespace(namespace).list().getItems(), MetaData.TYPE_JOB);
                default:
                    throw new CommandParseException("Unknown resource: " + resource);
            }
        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        }

    }

}
