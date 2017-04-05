package com.github.mlamina.api;

import java.util.ArrayList;
import java.util.List;

public class ParseCommandResponse {

    private boolean valid;
    private List<String> possibleCompletions = new ArrayList<>();

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getPossibleCompletions() {
        return possibleCompletions;
    }

    public void setPossibleCompletions(List<String> possibleCompletions) {
        this.possibleCompletions = possibleCompletions;
    }
}
