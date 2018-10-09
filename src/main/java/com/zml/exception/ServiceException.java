package com.zml.exception;

public class ServiceException extends RuntimeException{
    public ServiceException(){}

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable th,String message) {
        super(message,th);
    }
}
