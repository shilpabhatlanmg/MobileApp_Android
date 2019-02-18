package com.protectapp.model;

public class GenericResponseModel<T> {
    private int success;
    private String message;
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public T getData ()
    {
        return data;
    }

    public void setData (T data)
    {
        this.data = data;
    }

}
