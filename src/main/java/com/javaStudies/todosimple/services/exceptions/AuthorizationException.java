package com.javaStudies.todosimple.services.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AuthorizationException extends AccessDeniedException {
    private static final long serialVersionUID = 1L;

	public AuthorizationException(String message) {
        super(message);
    }
}
