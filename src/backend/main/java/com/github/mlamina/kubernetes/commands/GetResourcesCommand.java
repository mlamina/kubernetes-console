package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetResourcesCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        return Pattern.compile("^get\\s(deployments|pods|services|jobs|nodes)$");
    }

    @Override
    public Object execute(KubernetesClient client) throws CommandParseException {
        try {
            Matcher m = getRegExp().matcher(getRawCommand());
            if (!m.find())
                throw new CommandParseException("Unable to parse command: " + getRawCommand());
            String resource = m.group(1);
            switch (resource) {
                case "deployments": return client.extensions().deployments().list().getItems();
                case "pods": return client.pods().list().getItems();
                case "services": return client.services().list().getItems();
                case "jobs": return client.extensions().jobs().list().getItems();
                case "nodes": return client.nodes().list().getItems();
                default:
                    throw new CommandParseException("Unknown resource: " + resource);
            }
        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        }

    }

}
