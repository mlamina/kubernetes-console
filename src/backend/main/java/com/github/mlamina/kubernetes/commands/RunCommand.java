package com.github.mlamina.kubernetes.commands;

import com.github.mlamina.api.MetaData;
import com.github.mlamina.api.MetaResponse;
import com.github.mlamina.api.ResponseError;
import com.github.mlamina.kubernetes.Command;
import com.github.mlamina.kubernetes.CommandParseException;
import com.github.mlamina.kubernetes.ResourceCache;
import io.fabric8.kubernetes.client.Callback;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.utils.InputStreamPumper;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RunCommand extends Command implements ExecListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunCommand.class);
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private boolean done = false;
    private boolean success = false;

    @Override
    protected Pattern getRegExp() {
        // run "{command}" in {namespace}/{pod}"
        List<String> pods = ResourceCache.INSTANCE.get("pod")
                .stream()
                .map((pod) -> String.format("%s/%s", pod.getMetadata().getNamespace(), pod.getMetadata().getName()))
                .collect(Collectors.toList());
        String regex = "^run\\s\"(.+)\"\\sin\\s(" + StringUtils.join(pods, "|") + ")$";
        return Pattern.compile(regex);
    }

    @Override
    public MetaResponse execute(KubernetesClient client) throws CommandParseException {
        try {
            Matcher m = getRegExp().matcher(getRawCommand());
            if (!m.find())
                throw new CommandParseException("Unable to parse command: " + getRawCommand());
            String command = m.group(1);
            String[] podDescriptor = m.group(2).split("/");
            String podName = podDescriptor[1];
            String namespace = podDescriptor[0];
            LOGGER.info("Executing shell command \"{}\" in {}/{}", command, namespace, podName);
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (
                    ExecWatch watch = client.pods().inNamespace(namespace).withName(podName)
                            .redirectingInput()
                            .redirectingOutput()
                            .exec();

                    InputStreamPumper pump = new InputStreamPumper(watch.getOutput(), bytes -> {
                        try {
                            out.write(bytes);
                            executorService.shutdownNow();
                        } catch (IOException e) {
                            LOGGER.error("Failed to write command output", e);
                        }
                    }, () -> LOGGER.info("Done!")))
            {

                executorService.submit(pump);
                watch.getInput().write((command + "\n").getBytes());
                executorService.awaitTermination(20, TimeUnit.SECONDS);
                return MetaResponse.resource(out.toString(), MetaData.TYPE_BASH_OUTPUT);
            } catch (Exception e) {
                ResponseError error = new ResponseError(ResponseError.CODE_RUN_FAILED, e.getMessage());
                return MetaResponse.error(error);
            } finally {
                executorService.shutdownNow();
            }

        } catch (IllegalStateException e) {
            throw new CommandParseException("Unable to parse command: " + getRawCommand());
        } catch (KubernetesClientException e) {
            ResponseError error = new ResponseError(ResponseError.CODE_RUN_FAILED, e.getMessage());
            return MetaResponse.error(error);
        }

    }

    @Override
    public void onOpen(Response response) {
        LOGGER.debug("Opened shell");
    }

    @Override
    public void onFailure(Throwable throwable, Response response) {
        LOGGER.error("Failed to execute shell command", throwable);
        done = true;
        success = false;
    }

    @Override
    public void onClose(int code, String reason) {
        LOGGER.debug("Closing shell");
        done = true;
        success = true;
    }
}
