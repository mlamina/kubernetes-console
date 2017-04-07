package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.GetResourcesCommand;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTest {


    @Test
    public void testFindGetResourcesCommand() throws CommandParseException {
        CommandParser parser = new CommandParser("get deployments");
        Optional<Command> result = parser.getCommand();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(GetResourcesCommand.class);
    }

}
