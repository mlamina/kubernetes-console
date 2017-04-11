package com.github.mlamina.kubernetes;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParseTreeTest {

    private CommandParseTree tree;

    public void setupResourceCache() {
        ResourceCache.INSTANCE.setNamespaces(Lists.newArrayList("default", "kube-system"));
        Pod pod = new Pod();
        pod.setMetadata(new ObjectMeta());
        pod.getMetadata().setName("pod1");
        pod.getMetadata().setNamespace("default");
        ResourceCache.INSTANCE.set("pod", Lists.newArrayList(pod));
    }

    @Before
    public void createTree() {
        setupResourceCache();
        tree = CommandParseTree.get();
    }


    @Test
    public void testParseCase1() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("get pods".split(" "));
        assertThat(parsed.size()).isEqualTo(3);
        assertThat(parsed.get(0).getPosition()).isEqualTo(0);
        assertThat(parsed.get(0).getValue()).isEqualTo("get");
        assertThat(parsed.get(0).isParsed()).isTrue();
        assertThat(parsed.get(0).isKnown()).isTrue();

        assertThat(parsed.get(1).getPosition()).isEqualTo(1);
        assertThat(parsed.get(1).getValue()).isEqualTo("pods");
        assertThat(parsed.get(1).isParsed()).isTrue();
        assertThat(parsed.get(1).isKnown()).isTrue();

        assertThat(parsed.get(2).getPosition()).isEqualTo(2);
        assertThat(parsed.get(2).getValue()).isEqualTo("");
        assertThat(parsed.get(2).isParsed()).isFalse();
        assertThat(parsed.get(2).isKnown()).isFalse();
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

    @Test
    public void testParseAddsPodNameCompletions() throws CommandParseException {
        List<CommandToken> parsed = tree.parse("from default get pod".split(" "));
        assertThat(parsed.size()).isEqualTo(5);
        assertThat(parsed.get(4).getPosition()).isEqualTo(4);
        assertThat(parsed.get(4).getCompletions().size()).isEqualTo(1);
        assertThat(parsed.get(4).getCompletions().get(0)).isEqualTo("pod1");
    }

}
