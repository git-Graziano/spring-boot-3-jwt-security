package com.alibou.security.auth.exception;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException(String notFound) {
        super(notFound);
    }

    public AuthorityNotFoundException(String notFound, Throwable cause) {
        super(notFound, cause);
    }

}
