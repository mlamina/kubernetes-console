package com.github.mlamina.kubernetes;

import com.github.mlamina.kubernetes.commands.GetResourceInNamespaceCommand;
import com.github.mlamina.kubernetes.commands.LogsCommand;
import com.github.mlamina.kubernetes.commands.RunCommand;
import com.github.mlamina.kubernetes.commands.ScaleDeploymentCommand;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
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

        Deployment deployment = new Deployment();
        deployment.setMetadata(new ObjectMeta());
        deployment.getMetadata().setName("dep");
        deployment.getMetadata().setNamespace("default");
    }

    @Test
    public void testLogsCommand() {
        LogsCommand command = new LogsCommand();
        assertThat(command.matches("logs default/es-saphire-3w6d1")).isTrue();
        assertThat(command.matches("logs default/pod2")).isFalse();
        assertThat(command.matches("logs kube-system/pod2")).isTrue();
        assertThat(command.matches("logs ")).isFalse();
        assertThat(command.matches("logs")).isFalse();
        assertThat(command.matches("logs pod1")).isFalse();
    }

    @Test
    public void testResourceInNamespace() throws CommandParseException {
        GetResourceInNamespaceCommand command = new GetResourceInNamespaceCommand();
        assertThat(command.matches("from default get pod es-saphire-3w6d1")).isTrue();
        assertThat(command.matches("from kube-system get pod pod2")).isTrue();
        assertThat(command.matches("from default get deployment dep")).isTrue();
        assertThat(command.matches("from default get deployments dep")).isFalse();
        assertThat(command.matches("from default get deployment ")).isFalse();
        assertThat(command.matches("from default get deployment")).isFalse();
        assertThat(command.matches("from default get pods es-saphire-3w6d1")).isFalse();
    }

    @Test
    public void testScaleDeployment() throws CommandParseException {
        ScaleDeploymentCommand command = new ScaleDeploymentCommand();
        assertThat(command.matches("scale default/dep 0")).isTrue();
        assertThat(command.matches("scale default/dep 10")).isTrue();
        assertThat(command.matches("scale default/otherdep 1")).isTrue();
        assertThat(command.matches("scale default/otherdep")).isFalse();
        assertThat(command.matches("scale default/otherdep ")).isFalse();
        assertThat(command.matches("scale default 0 ")).isFalse();
        assertThat(command.matches("scale dep 0 ")).isFalse();
    }

    @Test
    public void testRunCommand() throws CommandParseException {
        RunCommand command = new RunCommand();
        assertThat(command.matches("run \"bash\" in kube-system/unknown")).isFalse();
        assertThat(command.matches("run \"\" in kube-system/pod2")).isFalse();
        assertThat(command.matches("run \" in kube-system/pod2")).isFalse();
        assertThat(command.matches("run")).isFalse();
        assertThat(command.matches("run ")).isFalse();
        assertThat(command.matches("run \"bash\" in kube-system/pod2")).isTrue();
        assertThat(command.matches("run \"ls -la\" in kube-system/pod2")).isTrue();
    }

}
