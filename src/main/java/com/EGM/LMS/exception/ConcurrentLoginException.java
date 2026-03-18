package com.EGM.LMS.exception;

import lombok.Getter;

@Getter
public class ConcurrentLoginException extends RuntimeException {
    private final String code;

    public ConcurrentLoginException(String code, String message) {
        super(message);
        this.code = code;
    }
}

