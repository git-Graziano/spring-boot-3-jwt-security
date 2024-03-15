package com.alibou.security.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String ex) {
        super(ex);
    }
}
