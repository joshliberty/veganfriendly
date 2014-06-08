package com.joshliberty.veganfriendly.api;

/**
 * Created by caligula on 06/06/14.
 * This file is part of VeganFriendly.
 */
public class GenericApiResult<T> {
    private String error;
    private String type;
    private T data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
