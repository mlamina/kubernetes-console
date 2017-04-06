package com.github.mlamina.kubernetes;

import com.google.common.collect.Sets;

import java.util.Set;

public class ResourceNode extends CommandParseTree {

    private static final Set<String> SUPPORTED_RESOURCES =
            Sets.newHashSet("pods", "deployments", "services");

    public ResourceNode() {

    }

    @Override
    protected boolean canHandleToken(String token) {
        if (SUPPORTED_RESOURCES.contains(token)) {
            // Remember the token so that later we know which token this node is processing
            // TODO: Find a cleaner way to achieve this this
            this.token = token;
            return true;
        } else
            return false;
    }
}
