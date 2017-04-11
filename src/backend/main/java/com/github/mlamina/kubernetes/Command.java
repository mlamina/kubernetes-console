package com.github.mlamina.kubernetes;

import com.github.mlamina.api.MetaResponse;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {

    private String rawCommand;

    protected abstract Pattern getRegExp();
    public abstract MetaResponse execute(KubernetesClient client) throws CommandParseException;

    public boolean matches(String rawCommand) {
        Matcher m = getRegExp().matcher(rawCommand);
        return m.find();
    }

    protected String getRawCommand() {
        return rawCommand;
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }
}
