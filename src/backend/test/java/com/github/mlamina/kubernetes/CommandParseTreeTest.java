package com.github.mlamina.kubernetes;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParseTreeTest {

    private CommandParseTree tree;

    @Before
    public void createTree() {
        tree = CommandParseTree.get();
    }


    @Test
    public void testParseCase1() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("get pods".split(" "));
        assertThat(parsed.size()).isEqualTo(2);
        assertThat(parsed.get(0).getPosition()).isEqualTo(0);
        assertThat(parsed.get(0).getValue()).isEqualTo("get");
        assertThat(parsed.get(0).isParsed()).isTrue();
        assertThat(parsed.get(0).isKnown()).isTrue();

        assertThat(parsed.get(1).getPosition()).isEqualTo(1);
        assertThat(parsed.get(1).getValue()).isEqualTo("pods");
        assertThat(parsed.get(1).isParsed()).isTrue();
        assertThat(parsed.get(1).isKnown()).isTrue();
    }

    @Test
    public void testParseIncompleteCommand() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("get".split(" "));
        assertThat(parsed.size()).isEqualTo(2);
        assertThat(parsed.get(0).getPosition()).isEqualTo(0);
        assertThat(parsed.get(0).getValue()).isEqualTo("get");
        assertThat(parsed.get(0).isParsed()).isTrue();
        assertThat(parsed.get(0).isKnown()).isTrue();

        assertThat(parsed.get(1).getPosition()).isEqualTo(1);
        assertThat(parsed.get(1).getValue()).isEqualTo("");
        assertThat(parsed.get(1).isParsed()).isFalse();
        assertThat(parsed.get(1).isKnown()).isFalse();
        assertThat(parsed.get(1).getCompletions().size()).isGreaterThan(0);
    }

    @Test
    public void testParseIncompleteCommand2() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("get p".split(" "));
        assertThat(parsed.size()).isEqualTo(2);
        assertThat(parsed.get(0).getPosition()).isEqualTo(0);
        assertThat(parsed.get(0).getValue()).isEqualTo("get");
        assertThat(parsed.get(0).isParsed()).isTrue();
        assertThat(parsed.get(0).isKnown()).isTrue();

        assertThat(parsed.get(1).getPosition()).isEqualTo(1);
        assertThat(parsed.get(1).getValue()).isEqualTo("p");
        assertThat(parsed.get(1).isParsed()).isFalse();
        assertThat(parsed.get(1).isKnown()).isFalse();
        assertThat(parsed.get(1).getCompletions().size()).isEqualTo(1);
        assertThat(parsed.get(1).getCompletions().get(0)).isEqualTo("pods");
    }

    @Test
    public void testParseEmptyCommand() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("".split(" "));
        assertThat(parsed.size()).isEqualTo(1);
        assertThat(parsed.get(0).getPosition()).isEqualTo(0);
        assertThat(parsed.get(0).getValue()).isEqualTo("");
        assertThat(parsed.get(0).isParsed()).isFalse();
        assertThat(parsed.get(0).isKnown()).isFalse();
        assertThat(parsed.get(0).getCompletions().size()).isGreaterThan(0);
    }

}
