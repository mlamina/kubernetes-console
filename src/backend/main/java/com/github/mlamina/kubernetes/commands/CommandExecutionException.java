package com.github.mlamina.kubernetes.commands;

public class CommandExecutionException extends Exception {

    private String command;
    private String message;

    public CommandExecutionException(String command, String message) {
        this.command = command;
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
