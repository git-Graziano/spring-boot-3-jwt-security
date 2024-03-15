package com.alibou.security.exception;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException(String notFound) {
        super(notFound);
    }

    public AuthorityNotFoundException(String notFound, Throwable cause) {
        super(notFound, cause);
    }

}
