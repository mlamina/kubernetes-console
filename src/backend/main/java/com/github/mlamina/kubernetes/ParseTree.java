package com.github.mlamina.kubernetes;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ParseTree {

    private static final Logger logger = LoggerFactory.getLogger("Command Parser");

    // Root
    protected ParseTree() {
        token = null;
        parent = null;
    }

    protected ParseTree(String token) {
        this.token = token;
    }

    private ParseTree parent = null;
    protected String token;

    private List<ParseTree> children = Lists.newArrayList();

    public ParseTree parse(String[] command) throws CommandParseException {
        if (isLeaf()) {
            // Leaf node
            if (command.length > 0)
                throw new CommandParseException("Reached leaf, but command contains more tokens");
            logger.info("Done! Successfully parsed last token {} in leaf {}", getToken(), this);
            return this;
        } else {
            // Regular node
            if (command.length == 0)
                throw new CommandParseException("Command incomplete");
            for (ParseTree child: children){
                if (child.canHandleToken(command[0])) {
                    logger.info("Next child: {} for token {}", child, command[0]);
                    return child.parse(ArrayUtils.remove(command, 0));
                }
            }
            // No child can handle the first token
            throw new CommandParseException(String.format("Unknown token: %s", command[0]));
        }

    }

    protected boolean canHandleToken(String token) {
        return this.token.equals(token);
    }

    private boolean isLeaf() {
        return this.children.size() == 0;
    }

    public void addChild(ParseTree child) {
        child.setParent(this);
        children.add(child);
    }

    /**
     * Creates a new child with the given token.
     * @param token
     * @return The new child node
     */
    public ParseTree addChild(String token) {
        ParseTree child = new ParseTree(token);
        addChild(child);
        return child;
    }


    public ParseTree getParent() {
        return parent;
    }

    public void setParent(ParseTree parent) {
        this.parent = parent;
    }

    public String getToken() {
        return this.token;
    }
}
