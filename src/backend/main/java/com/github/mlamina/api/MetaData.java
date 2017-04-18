package com.github.mlamina.api;

public class MetaData {

    public static final String TYPE_LIST = "List";

    public static final String TYPE_TOKEN = "Token";
    public static final String TYPE_POD = "Pod";
    public static final String TYPE_SERVICE = "Service";
    public static final String TYPE_JOB = "Job";
    public static final String TYPE_NODE = "Node";
    public static final String TYPE_DEPLOYMENT = "Deployment";

    private String dataType, listType;


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }
}
