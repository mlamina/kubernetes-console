package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.LogsCommand;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRegexTests {

    @Before
    public void setupResourceCache() {
        ResourceCache.INSTANCE.setNamespaces(Lists.newArrayList("default", "kube-system"));
        Pod pod = new Pod();
        pod.setMetadata(new ObjectMeta());
        pod.getMetadata().setName("es-saphire-3w6d1");
        pod.getMetadata().setNamespace("default");
        Pod pod2 = new Pod();
        pod2.setMetadata(new ObjectMeta());
        pod2.getMetadata().setName("pod2");
        pod2.getMetadata().setNamespace("kube-system");
        ResourceCache.INSTANCE.set("pod", Lists.newArrayList(pod, pod2));
    }

    @Test
    public void testLogsCommand() {
        LogsCommand command = new LogsCommand();
        assertThat(command.matches("logs default/pod1")).isTrue();
        assertThat(command.matches("logs default/pod2")).isFalse();
        assertThat(command.matches("logs kube-system/pod2")).isTrue();
        assertThat(command.matches("logs ")).isFalse();
        assertThat(command.matches("logs")).isFalse();
        assertThat(command.matches("logs pod1")).isFalse();
    }

    @Test
    public void testLogging() throws CommandParseException {
        LogsCommand command = new LogsCommand();
        command.setRawCommand("logs default/es-saphire-3w6d1");
        command.execute(new DefaultKubernetesClient());
    }

}
