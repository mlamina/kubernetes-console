package com.github.mlamina.kubernetes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.Pod;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CommandParseTree {

    private static final Logger logger = LoggerFactory.getLogger("Command Parser");

    /**
     * Defines the console domain-specific language
     * @return the DSL's AST
     */
    public static CommandParseTree get() {
        List<String> namespacedResourceTypes = ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes();
        List<String> nonNamespacedResourceTypes = ResourceCache.INSTANCE.getAvailableNonNamespacedResourceTypes();
        List<String> namespaces = ResourceCache.INSTANCE.getNamespaces();
        CommandParseTree tree = new CommandParseTree();
        // get ...
        CommandParseTree getNode = tree.addChild("get");
        // get {resources}
        namespacedResourceTypes.stream()
                .map((r) -> getNode.addChild(r + "s"))
                // get {resources} in ...
                .map((node) -> node.addChild("in"))
                // get {resources} in {namespace}
                .flatMap((inNode) -> namespaces.stream().map(inNode::addChild))
                .forEach(CommandParseTree::addWatchFilter);
        // get {resources}
        nonNamespacedResourceTypes.stream()
                .map((r) -> getNode.addChild(r + "s"))
                .forEach(CommandParseTree::addWatchFilter);
        // from ...
        CommandParseTree fromNode = tree.addChild("from");
        namespaces.stream()
                // from {namespace}
                .map(fromNode::addChild)
                // from {namespace} get ...
                .map((namespaceNode) -> namespaceNode.addChild("get"))
                // from {namespace} get {resourceType}
                .forEach((getInNamespaceNode) ->
                    namespacedResourceTypes.forEach((resourceType) -> {
                        // from {namespace} get {resourceType} {resourceName}
                        CommandParseTree getResourceInNamespaceNode = getInNamespaceNode.addChild(resourceType);
                        ResourceCache.INSTANCE.get(resourceType)
                                .stream()
                                // filter resources by namespace
                                .filter((resource) -> resource.getMetadata().getNamespace().equals(getInNamespaceNode.getParent().getToken()))
                                .map((resource) -> getResourceInNamespaceNode.addChild(resource.getMetadata().getName()))
                                .forEach(CommandParseTree::addWatchFilter);
                    })
                );
        // logs ...
        CommandParseTree logsNode = tree.addChild("logs");
        ResourceCache.INSTANCE.get("pod").stream()
                .map((pod) -> String.format("%s/%s", pod.getMetadata().getNamespace(), pod.getMetadata().getName()))
                .map(logsNode::addChild)
                .forEach(CommandParseTree::addWatchFilter);
        // run {command} in {pod}[/{container}]
        CommandParseTree runCommandNode = tree.addChild("run").addVariable("command").addChild("in");
        ResourceCache.INSTANCE.get("pod").stream()
                .map((pod) -> String.format("%s/%s", pod.getMetadata().getNamespace(), pod.getMetadata().getName()))
                .forEach(runCommandNode::addChild);
        // scale ...
        CommandParseTree scaleNode = tree.addChild("scale");
        ResourceCache.INSTANCE.get("deployment")
                .stream()
                // scale {namespace}/{deploymentName} ...
                .map((deployment) -> scaleNode.addChild(String.format(
                        "%s/%s",
                        deployment.getMetadata().getNamespace(),
                        deployment.getMetadata().getName())))
                // scale {namespace}/{deploymentName} {replicas}
                .forEach((deploymentNode) -> deploymentNode.addVariable("replicas"));
        return tree;
    }

    private static CommandParseTree addWatchFilter(CommandParseTree node) {
        return node.addChild("|").addChild("watch");
    }

    // Root
    private CommandParseTree() {
        token = null;
        parent = null;
    }

    private CommandParseTree(String token) {
        this.token = token;
    }

    private CommandParseTree parent = null;
    protected String token;
    private boolean isVariable = false;

    private List<CommandParseTree> children = Lists.newArrayList();

    public List<CommandToken> parse(String[] command) {
        if (isLeaf()) {
            // Leaf node
            List<CommandToken> result = Lists.newArrayList();
            CommandToken current = new CommandToken(getToken(), countParents() - 1);
            current.setKnown(true);
            current.setParsed(true);
            current.setVariable(isVariable());
            result.add(current);

            // Add unparsed tokens to the end of the list
            for (int i=0; i < command.length; i++) {
                CommandToken unparsed = new CommandToken(command[i], current.getPosition() + i + 1);
                result.add(unparsed);
            }
            return result;
        } else {
            if (isRoot()) {
                // Root doesn't contain a token
                for (CommandParseTree child: children){
                    if (child.canHandleToken(command[0])) {
                        return child.parse(ArrayUtils.remove(command, 0));
                    }
                }
                // First token could not be handled by a child!
                List<CommandToken> result = Lists.newArrayList();
                // Return list of unparsed tokens
                for (int i=0; i < command.length; i++) {
                    CommandToken unparsed = new CommandToken(command[i], i);
                    result.add(unparsed);
                }
                result.get(0).setCompletions(getCompletionsFor(result.get(0).getValue()));
                return result;
            } else {
                // Neither root nor leaf
                CommandToken current = new CommandToken(getToken(), countParents() - 1);
                current.setKnown(true);
                current.setParsed(true);
                current.setVariable(isVariable());
                if (command.length == 0) {
                    // Current node expects additional tokens
                    CommandToken suggestion = new CommandToken("", current.getPosition() + 1);
                    suggestion.setCompletions(getCompletionsFor(""));
                    return Lists.newArrayList(current, suggestion);
                }
                // Try to match next token
                for (CommandParseTree child: children){
                    if (child.canHandleToken(command[0])) {
                        List<CommandToken> result = child.parse(ArrayUtils.remove(command, 0));
                        // If child was a variable, replace variable name with user value
                        if (result.size() > 0 && result.get(0).isVariable())
                            result.get(0).setValue(command[0]);
                        // Add current node to front of list
                        result.add(0, current);
                        return result;
                    }
                }
                // First token could not be handled by a child!
                List<CommandToken> result = Lists.newArrayList(current);
                // Add unparsed tokens to the end of the list
                for (int i=0; i < command.length; i++) {
                    CommandToken unparsed = new CommandToken(command[i], current.getPosition() + i + 1);
                    if (i==0)
                        unparsed.setCompletions(getCompletionsFor(command[i]));
                    result.add(unparsed);
                }
                return result;
            }
        }
    }

    private List<String> getCompletionsFor(String token) {
        return children.stream()
                .filter((t) -> t.getToken().startsWith(token) || t.isVariable())
                .map((node) -> {
                    if (node.isVariable())
                        return String.format("{%s}", node.getToken());
                    else
                        return node.getToken();
                })
                .collect(Collectors.toList());
    }

    private int countParents() {
        int count = 0;
        CommandParseTree node = this.getParent();
        while (node != null) {
            node = node.getParent();
            count++;
        }
        return count;
    }



    private boolean canHandleToken(String token) {
        Preconditions.checkNotNull(token);
        if (isVariable)
            return true;
        return this.token.equals(token);
    }

    private boolean isLeaf() {
        return this.children.size() == 0;
    }

    private boolean isRoot() {
        return this.getParent() == null;
    }

    public void addChild(CommandParseTree child) {
        child.setParent(this);
        children.add(child);
    }

    /**
     * Creates a new child with the given token.
     * @param token
     * @return The new child node
     */
    private CommandParseTree addChild(String token) {
        CommandParseTree child = new CommandParseTree(token);
        addChild(child);
        return child;
    }

    private CommandParseTree addVariable(String name) {
        CommandParseTree child = new CommandParseTree(name);
        child.isVariable = true;
        addChild(child);
        return child;
    }

    @Override
    public String toString() {
        return String.format("%s", this.token);
    }

    public CommandParseTree getParent() {
        return parent;
    }

    public void setParent(CommandParseTree parent) {
        this.parent = parent;
    }

    public String getToken() {
        return this.token;
    }

    public boolean isVariable() {
        return isVariable;
    }
}
