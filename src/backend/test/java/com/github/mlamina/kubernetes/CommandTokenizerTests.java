package com.github.mlamina.kubernetes;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandTokenizerTests {

    @Test
    public void testSimpleCommand() {
        CommandTokenizer tokenizer = new CommandTokenizer("from default get pod pod1");
        String[] tokens = tokenizer.tokenize();
        assertThat(tokens.length).isEqualTo(5);
        assertThat(tokens[0]).isEqualTo("from");
        assertThat(tokens[4]).isEqualTo("pod1");
    }

    @Test
    public void testTokenizesRunCommand() {
        CommandTokenizer tokenizer = new CommandTokenizer("run \"ls -la\" in namespace/pod");
        String[] tokens = tokenizer.tokenize();
        assertThat(tokens.length).isEqualTo(4);
        assertThat(tokens[0]).isEqualTo("run");
        assertThat(tokens[1]).isEqualTo("\"ls -la\"");
        assertThat(tokens[2]).isEqualTo("in");
        assertThat(tokens[3]).isEqualTo("namespace/pod");
    }

    @Test
    public void testRecognizesIncompleteVariable() {
        CommandTokenizer tokenizer = new CommandTokenizer("run \"ls -la");
        String[] tokens = tokenizer.tokenize();
        assertThat(tokens.length).isEqualTo(2);
        assertThat(tokens[0]).isEqualTo("run");
        assertThat(tokens[1]).isEqualTo("\"ls -la");
    }

    @Test
    public void testRecognizesIncompleteVariableWithTrailingSpace() {
        CommandTokenizer tokenizer = new CommandTokenizer("run \"ls -la ");
        String[] tokens = tokenizer.tokenize();
        assertThat(tokens.length).isEqualTo(2);
        assertThat(tokens[0]).isEqualTo("run");
        assertThat(tokens[1]).isEqualTo("\"ls -la ");
    }

}
