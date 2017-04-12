package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.LogResult;
import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.api.ResponseError;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.ResourceCache;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogsCommand extends Command {
    @Override
    protected Pattern getRegExp() {
        // "logs {namespace}/{pod}"
        List<String> pods = ResourceCache.INSTANCE.get("pod")
                .stream()
                .map((pod) -> String.format("%s/%s", pod.getMetadata().getNamespace(), pod.getMetadata().getName()))
                .collect(Collectors.toList());
        String regex = "^logs\\s(" + StringUtils.join(pods, "|") + ")$";
        return Pattern.compile(regex);
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException {
        try {
            Matcher m = getRegExp().matcher(getRawCommand());
            if (!m.find())
                throw new CommandParseException("Unable to parse command: " + getRawCommand());
            String[] podDescriptor = m.group(1).split("/");
            String podName = podDescriptor[1];
            String namespace = podDescriptor[0];
            PodResource podResource = client.pods().inNamespace(namespace).withName(podName);
            Pod pod = (Pod) podResource.get();
            List<LogResult> results = pod.getSpec().getContainers().stream().map((container) -> {
                String log = client.pods()
                        .inNamespace(namespace)
                        .withName(podName)
                        .inContainer(container.getName())
                        .getLog();
                return new LogResult(container.getName(), log);
            }).collect(Collectors.toList());
            return MetaResponse.list(results, "Logs");

        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        } catch (KubernetesClientException e) {
            ResponseError error = new ResponseError(ResponseError.CODE_LOG_FAILED, e.getMessage());
            return MetaResponse.error(error);
        }

    }

}
