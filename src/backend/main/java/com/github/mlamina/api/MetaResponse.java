package com.github.mlamina.api;

import java.util.Collection;

public class MetaResponse {

    private MetaResponse() {}

    public static MetaResponse success(Object data) {
        MetaResponse response = new MetaResponse();
        response.data = data;
        return response;
    }

    private Object data = null;
    private Collection<ResponseError> errors;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Collection<ResponseError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<ResponseError> errors) {
        this.errors = errors;
    }
}
