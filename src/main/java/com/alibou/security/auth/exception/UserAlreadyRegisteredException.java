package com.alibou.security.auth.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String notFound) {
        super(notFound);
    }
}
