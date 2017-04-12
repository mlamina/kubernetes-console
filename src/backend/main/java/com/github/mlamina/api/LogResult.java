package com.github.mlamina.api;

public class LogResult {

    private String container;
    private String logs;

    public LogResult(String container, String logs) {
        this.container = container;
        this.logs = logs;
    }

    public String getContainer() {
        return container;
    }

    public String getLogs() {
        return logs;
    }

}
