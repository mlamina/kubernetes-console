package com.github.mlamina.api;

import com.google.common.collect.Sets;

import java.util.Collection;

public class MetaResponse {

    private MetaResponse() {}

    public static MetaResponse success(Object data, MetaData meta) {
        MetaResponse response = new MetaResponse();
        response.data = data;
        response.meta = meta;
        return response;
    }

    public static MetaResponse list(Collection<?> data, String listType) {
        MetaResponse response = new MetaResponse();
        response.data = data;
        MetaData metaData = new MetaData();
        metaData.setDataType(MetaData.TYPE_LIST);
        metaData.setListType(listType);
        response.meta = metaData;
        return response;
    }

    public static MetaResponse resource(Object data, String resourceType) {
        MetaResponse response = new MetaResponse();
        response.data = data;
        MetaData metaData = new MetaData();
        metaData.setDataType(resourceType);
        response.meta = metaData;
        return response;
    }



    private Object data = null;
    private MetaData meta;
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

    public static MetaResponse error(ResponseError ...responseErrors) {
        MetaResponse response = new MetaResponse();
        response.errors = Sets.newHashSet(responseErrors);
        return response;
    }

    public MetaData getMeta() {
        return meta;
    }
}
