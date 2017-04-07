package com.github.mlamina.api;

import javax.validation.constraints.NotNull;

public class ExecuteCommandRequest {

    @NotNull
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
