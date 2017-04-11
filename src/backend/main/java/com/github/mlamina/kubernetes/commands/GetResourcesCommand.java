package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Listable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetResourcesCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        return Pattern.compile("^get\\s(deployments|pods|services|jobs|nodes)$");
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
                    return MetaResponse.list(client.extensions().deployments().list().getItems(), MetaData.LIST_TYPE_DEPLOYMENT);
                case "pods":
                    return MetaResponse.list(client.pods().list().getItems(), MetaData.LIST_TYPE_POD);
                case "services":
                    return MetaResponse.list(client.services().list().getItems(), MetaData.LIST_TYPE_SERVICE);
                case "jobs":
                    return MetaResponse.list(client.extensions().jobs().list().getItems(), MetaData.LIST_TYPE_JOB);
                case "nodes":
                    return MetaResponse.list(client.nodes().list().getItems(), MetaData.LIST_TYPE_NODE);
                default:
                    throw new CommandParseException("Unknown resource: " + resource);
            }
        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        }

    }

}
