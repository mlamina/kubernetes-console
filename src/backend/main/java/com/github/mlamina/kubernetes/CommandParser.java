package com.github.mlamina.kubernetes;

import java.util.List;

public class CommandParser {

    private final String rawCommand;

    public CommandParser(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    public List<CommandToken> getTokens() {
        String[] split = rawCommand.trim().split(" ");
        return CommandParseTree.get().parse(split);
    }
}
