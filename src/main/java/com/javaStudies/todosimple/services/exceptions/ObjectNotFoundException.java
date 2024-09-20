package com.javaStudies.todosimple.services.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = -281562150853600189L;

	public ObjectNotFoundException(String message) {
        super(message);
    }

}