package com.github.mlamina.kubernetes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
        List<String> resourceTypes = ResourceCache.INSTANCE.getAvailableNamespacedResourceTypes();
        List<String> namespaces = ResourceCache.INSTANCE.getNamespaces();
        CommandParseTree tree = new CommandParseTree();
        // get ...
        CommandParseTree getNode = tree.addChild("get");
        // get {resources}
        resourceTypes.stream()
                .map((r) -> getNode.addChild(r + "s"))
                // get {resources} in ...
                .map((node) -> node.addChild("in"))
                // get {resources} in {namespace}
                .forEach((inNode) -> namespaces.forEach(inNode::addChild));
        // watch ...
        tree.addChild("watch");
        // run ,,,
        tree.addChild("run");
        return tree;
    }

    // Root
    protected CommandParseTree() {
        token = null;
        parent = null;
    }

    protected CommandParseTree(String token) {
        this.token = token;
    }

    private CommandParseTree parent = null;
    protected String token;

    private List<CommandParseTree> children = Lists.newArrayList();

    public List<CommandToken> parse(String[] command) {
        if (isLeaf()) {
            // Leaf node
            List<CommandToken> result = Lists.newArrayList();
            CommandToken current = new CommandToken(getToken(), countParents() - 1);
            current.setKnown(true);
            current.setParsed(true);
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
                if (command.length == 0) {
                    // Current node expects additional tokens
                    CommandToken suggestion = new CommandToken("", current.getPosition() + 1);
                    suggestion.setCompletions(getCompletionsFor(""));
                    return Lists.newArrayList(current, suggestion);
                }
                // Try to match next token
                for (CommandParseTree child: children){
                    if (child.canHandleToken(command[0])) {
                        logger.info("Next child: {} for token {}", child, command[0]);
                        List<CommandToken> result = child.parse(ArrayUtils.remove(command, 0));
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
                .map(CommandParseTree::getToken)
                .filter((t) -> t.startsWith(token))
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



    protected boolean canHandleToken(String token) {
        Preconditions.checkNotNull(token);
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
    public CommandParseTree addChild(String token) {
        CommandParseTree child = new CommandParseTree(token);
        addChild(child);
        return child;
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
}
