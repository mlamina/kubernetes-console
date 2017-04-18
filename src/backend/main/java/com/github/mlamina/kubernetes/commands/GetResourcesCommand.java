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

public class GetResourcesCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        // "get {resourceType}s"
        List<String> allResourceTypes = ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes();
        allResourceTypes.addAll(ResourceCache.INSTANCE.getAvailableNonNamespacedResourceTypes());
        allResourceTypes = allResourceTypes.stream().map((r) -> r + "s").collect(Collectors.toList());
        String regex = "^get\\s(" + StringUtils.join(allResourceTypes, "|") + ")$";
        return Pattern.compile(regex);
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException {
        try {
            Matcher m = getRegExp().matcher(getRawCommand());
            if (!m.find())
                throw new CommandParseException("Unable to parse command: " + getRawCommand());
            String resource = m.group(1);
            switch (resource) {
                case "deployments":
                    return MetaResponse.list(client.extensions().deployments().list().getItems(), MetaData.TYPE_DEPLOYMENT);
                case "pods":
                    return MetaResponse.list(client.pods().list().getItems(), MetaData.TYPE_POD);
                case "services":
                    return MetaResponse.list(client.services().list().getItems(), MetaData.TYPE_SERVICE);
                case "jobs":
                    return MetaResponse.list(client.extensions().jobs().list().getItems(), MetaData.TYPE_JOB);
                case "nodes":
                    return MetaResponse.list(client.nodes().list().getItems(), MetaData.TYPE_NODE);
                default:
                    throw new CommandParseException("Unknown resource: " + resource);
            }
        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        }

    }

}
