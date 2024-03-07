package com.alibou.security.auth;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException(String notFound) {
        super(notFound);
    }

    public AuthorityNotFoundException(String notFound, Throwable cause) {
        super(notFound, cause);
    }

}
