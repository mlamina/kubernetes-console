package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.GetResourcesCommand;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CommandParser {

    private final String rawCommand;
    private final Set<Command> commands = Sets.newHashSet(new GetResourcesCommand());

    public CommandParser(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    public List<CommandToken> getTokens() {
        String[] split = rawCommand.trim().split(" ");
        return CommandParseTree.get().parse(split);
    }

    public Optional<Command> getCommand() {
        Optional<Command> commandOptional = commands.stream()
                .filter((command) -> command.matches(rawCommand))
                .findFirst();
        if (commandOptional.isPresent())
            commandOptional.get().setRawCommand(rawCommand);
        return commandOptional;
    }
}
