package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.GetResourceInNamespaceCommand;
import com.github.mlamina.kubernetes.commands.GetResourcesInNamespaceCommand;
import com.github.mlamina.kubernetes.commands.GetResourcesCommand;
import com.github.mlamina.kubernetes.commands.LogsCommand;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Takes a raw user command (e.g. "get pods in kube-system") and attempts to find a matching
 * Command instance.
 */
public class CommandParser {

    private final String rawCommand;
    private final Set<Command> commands = Sets.newHashSet(
            new LogsCommand(),
            new GetResourcesCommand(),
            new GetResourceInNamespaceCommand(),
            new GetResourcesInNamespaceCommand());

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
        commandOptional.ifPresent(command -> command.setRawCommand(rawCommand));
        return commandOptional;
    }
}
