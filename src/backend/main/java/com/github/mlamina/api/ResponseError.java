package com.github.mlamina.api;

public class ResponseError {

    public static final int CODE_COMMAND_PARSING_FAILED = 100;
    public static final int CODE_COMMAND_EXECUTION_FAILED = 101;
    public static final int CODE_COMMAND_NOT_FOUND = 102;
    public static final int CODE_LOG_FAILED = 200;
    public static final int CODE_RUN_FAILED = 301;

    public ResponseError(int code, String message) {
        this.message = message;
        this.code = code;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
