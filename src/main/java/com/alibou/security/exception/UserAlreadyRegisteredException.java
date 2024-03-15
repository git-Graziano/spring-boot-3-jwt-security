package com.alibou.security.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String notFound) {
        super(notFound);
    }
}
