package com.github.mlamina.kubernetes;

import com.google.common.collect.Lists;

import java.util.List;

public class CommandToken {

    private String value;
    private int position;
    private boolean parsed, known = false;
    private boolean variable = false;
    private List<String> completions = Lists.newArrayList();


    public CommandToken(String value, int position) {
        this.value = value;
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public boolean isParsed() {
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    public List<String> getCompletions() {
        return completions;
    }

    public void setCompletions(List<String> completions) {
        this.completions = completions;
    }

    public boolean isVariable() {
        return variable;
    }

    public void setVariable(boolean variable) {
        this.variable = variable;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
