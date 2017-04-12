package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.GetResourcesCommand;
import com.github.mlamina.kubernetes.commands.GetResourcesInNamespaceCommand;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTest {


    @Before
    public void setupResourceCache() {
        ResourceCache.INSTANCE.setNamespaces(Lists.newArrayList("default", "kube-system"));
    }

    @Test
    public void testParse() throws CommandParseException {
        assertThat(new CommandParser("get").getTokens().size()).isEqualTo(2);
        assertThat(new CommandParser("get deployments").getTokens().size()).isEqualTo(3);
        assertThat(new CommandParser("get pods").getTokens().size()).isEqualTo(3);
        assertThat(new CommandParser("get pods").getTokens().size()).isEqualTo(3);
    }

    @Test
    public void testFindGetResourcesCommand() throws CommandParseException {
        List<String> allResources = Lists.newArrayList(ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes());
        allResources.addAll(ResourceCache.INSTANCE.getAvailableNonNamespacedResourceTypes());
        for (String resource: allResources) {
            String command = "get " + resource + "s";
            CommandParser parser = new CommandParser(command);
            Optional<Command> result = parser.getCommand();
            assertThat(result.isPresent()).withFailMessage("Command not found: %s", command).isTrue();
            assertThat(result.get()).isInstanceOf(GetResourcesCommand.class);
        }
    }

    @Test
    public void testFindGetResourcesInNamespaceCommand() throws CommandParseException {
        for (String resource: ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes()) {
            for (String namespace: ResourceCache.INSTANCE.getNamespaces()) {
                String command = "get " + resource + "s in " + namespace;
                CommandParser parser = new CommandParser(command);
                Optional<Command> result = parser.getCommand();
                assertThat(result.isPresent()).withFailMessage("Command not found: %s", command).isTrue();
                assertThat(result.get()).isInstanceOf(GetResourcesInNamespaceCommand.class);
            }
        }
    }

}
