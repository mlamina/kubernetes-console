package com.github.mlamina.kubernetes;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParseTreeTest {

    private ParseTree tree;

    @Before
    public void createTree() {
        tree = new ParseTree();
        ParseTree getNode = tree.addChild("get");
        getNode.addChild(new ResourceNode());
        tree.addChild("watch");
    }


    @Test
    public void testParseCase1() throws CommandParseException {
        ParseTree leaf = tree.parse("get pods".split(" "));
        assertThat(leaf.getToken()).isEqualTo("pods");
        assertThat(leaf.getParent().getParent()).isSameAs(tree);
    }

    @Test(expected = CommandParseException.class)
    public void testParseIncompleteCommand() throws CommandParseException {
        tree.parse("get".split(" "));
    }

    @Test(expected = CommandParseException.class)
    public void testParseEmptyCommand() throws CommandParseException {
        tree.parse("".split(" "));
    }

}
