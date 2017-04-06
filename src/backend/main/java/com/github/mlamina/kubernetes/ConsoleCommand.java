package com.github.mlamina.kubernetes;

import java.util.List;

public class ConsoleCommand {

    private final String[] command;

    public ConsoleCommand(String command) {
        this.command = command.split(" ");
    }

    public boolean isComplete() {
        return false;
    }

    public List<String> getCompletionsForLastTokens() {
        return null;
    }
}
