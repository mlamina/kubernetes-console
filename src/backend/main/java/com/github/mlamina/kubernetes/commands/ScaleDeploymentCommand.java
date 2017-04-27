package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.DeploymentBundle;
import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.ResourceCache;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScaleDeploymentCommand extends Command {

    protected static final Logger logger = LoggerFactory.getLogger(ScaleDeploymentCommand.class);

    @Override
    protected Pattern getRegExp() {
        // "scale {namespace}/{deployment} {replicas}"
        return Pattern.compile("^scale\\s(\\S+)/(\\S+)\\s(\\d+)$");
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException, CommandExecutionException {
        Matcher m = getRegExp().matcher(getRawCommand());
        if (!m.find())
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        String namespace = m.group(1);
        String deploymentName = m.group(2);
        int replicas = Integer.parseInt(m.group(3));
        logger.info("Scaling {}/{} to {}", namespace, deploymentName, replicas);
        Deployment deployment = client.extensions().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .get();
        if (deployment == null)
            throw new CommandExecutionException(
                getRawCommand(),
                String.format("No deployment '%s' found in namespace '%s'", deploymentName, namespace));
        deployment.getSpec().setReplicas(replicas);
        List<Pod> pods = client.pods()
                .inNamespace(namespace)
                .withLabels(deployment.getSpec().getSelector().getMatchLabels())
                .list().getItems();
        deployment = client.resource(deployment).createOrReplace();
        return MetaResponse.resource(new DeploymentBundle(deployment, pods), MetaData.TYPE_DEPLOYMENT_BUNDLE);
    }

}
